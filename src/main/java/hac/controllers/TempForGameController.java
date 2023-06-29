//package hac.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import hac.classes.forGame.UserTurn;
//import hac.repo.board.Board;
//import hac.repo.player.Player;
//import hac.repo.player.PlayerRepository;
//import hac.repo.room.Room;
//import hac.services.BoardService;
//import hac.services.PlayerService;
//import hac.services.RoomService;
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.ConstraintViolationException;
//import jakarta.validation.Validator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.security.Principal;
//import java.util.List;
//import java.util.Set;
//
//
////TODO - delete this, it's a temporary route
//@Controller
//@RequestMapping("/game/test")
//public class TempForGameController {
//
//    @Autowired
//    private RoomService roomService;
//
//    @Autowired
//    private PlayerService playerService;
//
//    @Autowired
//    private PlayerRepository playerRepository;
////    @Autowired
////    private RoomRepository roomRepo;
////
////    @Autowired
////    private PlayerRepository playersRepo;
//
//    @Autowired
//    BoardService boardService;
//
//    @GetMapping("")
//    public String tempGame(Principal principal){
//        try {
//
//            Player player1 = playerService.createNewPlayer("1");
//            Player player2 = playerService.createNewPlayer("2");
//            Room room = roomService.createNewRoom(player1, 0);
//            roomService.addPlayerToRoom(room.getId(),player2);
//            roomService.changeRoomStatus(room.getId(), Room.RoomEnum.WAITING_FOR_BOARDS);
//            ObjectMapper objectMapper = new ObjectMapper();
//            Board board1 = objectMapper.readValue("{\"submarines\":[{\"firstRow\":0,\"firstCol\":1,\"lastRow\":1,\"lastCol\":1,\"size\":2},{\"firstRow\":3,\"firstCol\":0,\"lastRow\":3,\"lastCol\":2,\"size\":3},{\"firstRow\":6,\"firstCol\":0,\"lastRow\":8,\"lastCol\":0,\"size\":3},{\"firstRow\":0,\"firstCol\":5,\"lastRow\":0,\"lastCol\":8,\"size\":4},{\"firstRow\":4,\"firstCol\":4,\"lastRow\":8,\"lastCol\":4,\"size\":5}]}", Board.class);
//            Board board2 = objectMapper.readValue("{\"submarines\":[{\"firstRow\":3,\"firstCol\":4,\"lastRow\":4,\"lastCol\":4,\"size\":2},{\"firstRow\":6,\"firstCol\":2,\"lastRow\":6,\"lastCol\":4,\"size\":3},{\"firstRow\":0,\"firstCol\":8,\"lastRow\":2,\"lastCol\":8,\"size\":3},{\"firstRow\":3,\"firstCol\":6,\"lastRow\":6,\"lastCol\":6,\"size\":4},{\"firstRow\":0,\"firstCol\":0,\"lastRow\":0,\"lastCol\":4,\"size\":5}]}", Board.class);
//            boardService.saveNewBoard(board1, "1");
//            boardService.saveNewBoard(board2,"2");
//            roomService.updateRoomStatusByUsername("1");
//            roomService.updateRoomStatusByUsername("2");
//            return "game/waitingForStartGame";
//        }
//        catch (Exception e){
//            System.out.println(e);
//            return "game/waitingForStartGame";
//        }
//        //return "redirect:/game/init";
//        //return "game/waitingForStartGame";
//    }
//
//    @GetMapping("/test")
//    public String temp(Principal principal){
////        try {
////            Player p = playerRepository.findByUsername("1");
////            p.setStatus(Player.PlayerStatus.ON_GAME);
////            playerRepository.save(p);
////        }
////        catch (Exception e){
////            System.out.println(e);
////        }
////        try{
////            Player player = playerService.getPlayerByUsername("1", true);
////            System.out.println(player.getInfo().toString());
////        }
////        catch (Exception e) {
////            System.out.println(e);
////        }
////        UserTurn userTurn = new UserTurn(1,1, "2");
////        roomService.setUpdates(principal.getName(), userTurn);
//        return "redirect:/game/on-game";
//    }
//}
