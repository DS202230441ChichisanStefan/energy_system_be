package com.platform.energy.grpc;

import com.platform.energy.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;

@GrpcService
public class ChatImpl extends ChatGrpc.ChatImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatImpl.class);

    private HashMap<String, StreamObserver<Message>> connectedUsers = new HashMap<>();

    @Override
    public void sendMessage(MessageRequest request, StreamObserver<Message> responseObserver) {
        LOGGER.info("Received client message: {} from user: {}", request.getMessage(), request.getUsername());

        // Get the user and message details sent from the client
        String receivedMessage = request.getMessage();
        String username = request.getUsername();
        Long timestamp = Instant.now().toEpochMilli();

        // Create a new message
        Message message = Message.newBuilder()
                .setMessage(receivedMessage)
                .setUsername(username)
                .setTimestamp(timestamp)
                .build();

        // Respond to the client who sent the message
        responseObserver.onNext(message);

        // Forward message to all clients
        connectedUsers.forEach((user, subscribedObserver) -> {
            try {
                subscribedObserver.onNext(message);
            } catch (Exception e) {
                // If the client is no longer connected, remove the user
            }
        });

        responseObserver.onCompleted();
    }

    @Override
    public void subscribe(SubscriptionRequest request, StreamObserver<Message> responseObserver) {
        String username = request.getUsername();
        Long timestamp = Instant.now().toEpochMilli();

        if (connectedUsers.containsKey(username)) {
            LOGGER.warn("Error: Username '{}' has already been taken.", username);

            Message serverErrorMessage = Message.newBuilder()
                    .setMessage("Error: Username '" + username + "' has already been taken.")
                    .setUsername("Server")
                    .setTimestamp(timestamp)
                    .build();

            responseObserver.onNext(serverErrorMessage);
            responseObserver.onCompleted();
        } else {
            Message serverSuccessMessage = Message.newBuilder()
                    .setMessage("Welcome to gRPC Chat, " + username + "!")
                    .setUsername("Server")
                    .setTimestamp(timestamp)
                    .build();

            responseObserver.onNext(serverSuccessMessage);

            // Store this client's StreamObserver
            connectedUsers.put(username, responseObserver);

            LOGGER.info("Client with username {} has subscribed.", username);
        }
    }

    @Override
    public void unsubscribe(UnsubscriptionRequest request, StreamObserver<Message> responseObserver) {
        String username = request.getUsername();
        Long timestamp = Instant.now().toEpochMilli();

        StreamObserver<Message> streamObserver = connectedUsers.get(username);

        if (streamObserver != null) {
            Message serverUnsubscribeMessage = Message
                    .newBuilder()
                    .setMessage("You have unsubscribed from gRPC Chat.")
                    .setUsername("Server")
                    .setTimestamp(timestamp)
                    .build();

            responseObserver.onNext(serverUnsubscribeMessage);
            responseObserver.onCompleted();

            // When a client has unsubscribed, close the connection.
            try {
                streamObserver.onCompleted();
            } catch (Exception e) {
                // StreamObserver already closed.
            }

            // Remove this client's StreamObserver.
            connectedUsers.remove(username);

            LOGGER.info("Client with username {} has been unsubscribed.", username);
        } else {
            LOGGER.warn("Client with username {} does not exist or has already unsubscribed. ", username);
        }
    }
}
