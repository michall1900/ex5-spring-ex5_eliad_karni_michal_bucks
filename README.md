## Authors
* Name: Eliad Karni  Email: eliadka@edu.hac.ac.il
* Name: Michal Bucks Email: michalbu@edu.hac.ac.il

## Note
We are using one extra day: Eliad has two. In solange permission, we can use one day and not lose points about that. 

## Description

The project is a battleships board game implementation.

### General information

Battleship (also known as Battleships or Sea Battle) is a strategy type guessing game for two players. It is played on ruled grids (paper or board) on which each player's fleet of warships are marked. The locations of the fleets are concealed from the other player. Players alternate turns calling "shots" at the other player's ships, and the objective of the game is to destroy the opposing player's fleet.  Battleship is known worldwide as a pencil and paper game which dates from World War I. It was published by various companies as a pad-and-pencil game in the 1930s and was released as a plastic board game by Milton Bradley in 1967. The game has spawned electronic versions, video games, smart device apps and a film.

The game is played on four grids, two for each player. The grids are typically square – usually 10×10 – and the individual squares in the grid are identified by letter and number. On one grid the player arranges ships and records the shots by the opponent. On the other grid, the player records their own shots. Before play begins, each player secretly arranges their ships on their primary grid. Each ship occupies a number of consecutive squares on the grid, arranged either horizontally or vertically. The number of squares for each ship is determined by the type of ship. The ships cannot overlap (i.e., only one ship can occupy any given square in the grid). The types and numbers of ships allowed are the same for each player. These may vary depending on the rules. The ships should be hidden from players sight and it's not allowed to see each other's pieces. The game is a discovery game which players need to discover their opponents ship positions:

The 1990 Milton Bradley version of the rules specify the following ships which contains:\
1 ship of the size of 2 tiles\
2 ship of the size of 3 tiles\
1 ship of the size of 4 tiles\
1 ship of the size of 5 tiles\

An alternative version of the rules specify the following ships:\
4 ship of the size of 1 tiles\
3 ship of the size of 2 tiles\
2 ship of the size of 3 tiles\
1 ship of the size of 4 tiles

After the ships have been positioned, the game proceeds in a series of rounds. In each round, each player takes a turn to announce a target square in the opponent's grid which is to be shot at. The opponent announces whether or not the square is occupied by a ship. If it is a "hit", the player who is hit marks this on their own or "ocean" grid (with a red peg in the pegboard version). The attacking player marks the hit or miss on their own "tracking" or "target" grid with a pencil marking in the paper version of the game, or the appropriate color peg in the pegboard version (red for "hit", white for "miss"), in order to build up a picture of the opponent's fleet. When all of the squares of a ship have been hit, the ship's owner announces the sinking of the Carrier, Submarine, Cruiser/Destroyer/Patrol Boat, or the titular Battleship. If all of a player's ships have been sunk, the game is over and their opponent wins. If all ships of both players are sunk by the end of the round (both players having had the same number of turns), the game is a draw.

### Functionality
Enter the route "/" (usually port 8080 of localhost).\
Read the How to play.\
Register to the server (None that the Username and the password length must be between 6 to 30 character long).\
Login to the server.\
Enter the lobby.\
Create a room or enter one.\
If creating a room, choose the game type then create the room.\
wait for all the players to join the room.\
Arrange your ships and submit your board.\
Wait for the other players to submit their board.\
Each round, wait for your turn and try to hit the opponent's ships.\
When one of the players won, you'll be moved into an informative page that indicate the winner.\
Back to lobby.\
If you want you can have another game!

## Installation
All the project is stored in memory and therefore no spacial installation is needed.

## Useful information
For easy usage, there are built in users:\
username: 0, password: 0, role: Admin\
username: 1, password: 1, role: User\
username: 2, password: 2, role: User

also, to see the database state you can preform a get rest to /game/test/print 