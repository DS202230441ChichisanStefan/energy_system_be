package com.platform.energy.services;

import com.platform.energy.dtos.UserDTO;
import com.platform.energy.entities.ERole;
import com.platform.energy.entities.Role;
import com.platform.energy.entities.User;
import com.platform.energy.mappers.Mappers;
import com.platform.energy.repo.IDeviceRepository;
import com.platform.energy.repo.IMeasurementRepository;
import com.platform.energy.repo.IRoleRepository;
import com.platform.energy.repo.IUserRepository;
import com.platform.energy.services.customexceptions.ConflictException;
import com.platform.energy.services.customexceptions.InvalidDataException;
import com.platform.energy.services.customexceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final IDeviceRepository iDeviceRepository;
    private final IMeasurementRepository iMeasurementRepository;
    private final Mappers mapper;

    public List<UserDTO> getAllUsers() throws ResourceNotFoundException {
        return iUserRepository.findAll().stream()
                .map(mapper::userToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUser(Long id) {
        Optional<User> foundUser = iUserRepository.findById(id);

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("User with id " + id + " not found.");
        }

        return mapper.userToDTO(foundUser.get());
    }

    public UserDTO getUserByUsername(String username) throws ResourceNotFoundException {
        Optional<User> foundUser = iUserRepository.findByUsername(username);

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("Users with username " + username + " not found.");
        }

        return mapper.userToDTO(foundUser.get());
    }

    public List<UserDTO> listUserClients() throws ResourceNotFoundException {
        return iUserRepository.findAllByRoleName(ERole.CLIENT).stream()
                .map(mapper::userToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO addUser(UserDTO givenUser) throws InvalidDataException {
        Optional<User> foundUser = iUserRepository.findByUsername(givenUser.getUsername());

        if (foundUser.isPresent()) {
            throw new ConflictException("Username " + givenUser.getUsername() + " already found. Cannot perform create operation.");
        }

        return mapper.userToDTO(iUserRepository.save(mapper.userToEntity(givenUser)));
    }

    public UserDTO updateUser(UserDTO user) throws ResourceNotFoundException {
        Optional<User> initialUser = iUserRepository.findById(user.getId());

        if (initialUser.isEmpty()) {
            throw new ResourceNotFoundException("User with id " + user.getId() + " cannot be found.");
        } else {
            return mapper.userToDTO(iUserRepository.save(mapper.userToEntity(user)));
        }
    }

    public void deleteUser(Long id) throws ResourceNotFoundException {
        Optional<User> foundUser = iUserRepository.findById(id);

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("Given user was not found. Delete operation could not be performed.");
        } else {
            iMeasurementRepository.deleteAllByDeviceUserId(id);
            iDeviceRepository.deleteAllByUserId(id);
            iUserRepository.delete(foundUser.get());
        }
    }

    public List<Role> listRoles() {
        return iRoleRepository.findAll();
    }

    public UserDTO login(String username, String password) {
        Optional<User> user = iUserRepository.findUserByUsernameAndPassword(username, password);

        return user.map(mapper::userToDTO).orElse(null);
    }
}
