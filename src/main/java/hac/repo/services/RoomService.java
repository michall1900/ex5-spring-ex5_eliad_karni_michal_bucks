package hac.repo.services;

import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RoomService {

    @Autowired
    private RoomRepository repository;

    @Transactional
    public void add(Room room) {
        repository.save(room);
    }

//    public void saveUser(User user) {
//        repository.save(user);
//    }
//    public void deleteUser(long id) {
//        repository.deleteById(id);
//    }
//    public void deleteUser(User u) {
//        repository.delete(u);
//    }
//    public void updateUser(User user) {
//        repository.save(user);
//    }
//    public Optional<User> getUser(long id) {
//        return repository.findById(id);
//    }
//
//    public List<User> getUsers() {
//        return repository.findAll();
//    }
}
