package com.platform.energy.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.platform.energy.dtos.DeviceDTO;
import com.platform.energy.dtos.MeasurementDTO;
import com.platform.energy.entities.Device;
import com.platform.energy.mappers.Mappers;
import com.platform.energy.repo.IDeviceRepository;
import com.platform.energy.services.MeasurementService;
import com.platform.energy.services.customexceptions.ResourceNotFoundException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Optional;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class Consumer {

    private static final String TASK_QUEUE_NAME = "smart_metering_device_simulator";
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    private final MeasurementService measurementService;

    private final IDeviceRepository iDeviceRepository;

    private final Mappers mappers;

    @PostConstruct
    public void init() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        boolean durable = false; // durable - RabbitMQ will never lose the queue if a crash occurs
        boolean exclusive = false; // exclusive - if queue only will be used by one connection
        boolean autoDelete = false; // autoDelete - queue is deleted when last consumer unsubscribe

        channel.queueDeclare(TASK_QUEUE_NAME, durable, exclusive, autoDelete, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+F2");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");

            try {
                MeasurementDTO measurementDTO = messageToDTO(message);
                measurementService.postMeasurement(measurementDTO);
            } catch (Exception e) {
                LOGGER.error("Can't insert measurement data." + message + ". The id is not found in the database.");
            }
        };

        channel.basicConsume(TASK_QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    public MeasurementDTO messageToDTO(String s) {
        StringBuilder builder = new StringBuilder(s);
        builder.deleteCharAt(s.length() - 1);
        builder.deleteCharAt(0);
        System.out.println(builder.toString());
        String[] splitString = builder.toString().split("\"");
        int i = 1;
        MeasurementDTO measurementDTO = new MeasurementDTO();
        while (i < splitString.length) {
            if (i == 1) {
                measurementDTO.setEnergyConsumption(Double.valueOf(splitString[i + 2]));
            }

            if (i == 5) {
                Long deviceId = Long.valueOf(splitString[i + 2]);
                Optional<Device> foundDevice = iDeviceRepository.findById(deviceId);
                if (foundDevice.isEmpty()) {
                    throw new ResourceNotFoundException("Given device was not found. Delete operation could not be performed.");
                }
                DeviceDTO deviceMapper = mappers.deviceToDTO(foundDevice.get());

                measurementDTO.setDevice(deviceMapper);
            }

            if (i == 9) {
                measurementDTO.setTimestamp(Timestamp.valueOf(splitString[i + 2]));
            }

            i = i + 2;
        }

        return measurementDTO;
    }
}
