<h1  style="color: #FFA500; font-family: 'Arial Black', sans-serif; font-size: 20px;">Manual test list for the View layer:</h1>

## Happy path tests:

-[x] Launch the game and verify that the main menu is displayed.
-[x] Insert a valid IP address and port and verify that the connection is established.
-[x] Verify that the player names are displayed correctly on the GUI.
-[x] Verify that the scores are updated correctly when the "scores updated" event is triggered.
-[x] Click on the "Start New Game" button and verify that a new game starts on all clients.
-[x] Verify that the hand of each player is updated correctly when the "hand updated" event is triggered.
-[x] Press SortScore button and verify that the hand is sorted by score.
-[x] Press SortABC button and verify that the hand is sorted by alphabet.
-[x] Click on your keyboard on a tile in the hand and verify that it gets selected and displayed on the game board.
-[x] Verify that the game board is updated correctly when the "tileBoard updated" event is triggered.
-[x] Try placing an invalid word on the game board and verify that an error message is displayed.
-[x] Try placing a valid word on the game board and verify that the word is placed correctly.
-[x] Try placing a word on the game board that is not valid in the game dictionaries and verify that it doesn't get
 placed.
-[x] Verify that the words for challenge are updated correctly when the "wordsForChallenge updated" event is triggered.
-[x] Click on the "Challenge" button and verify that the challenge is activated.
-[x] Verify that an error message is displayed if the challenge is already activated.
-[x] Pass the turn to the next player and verify that the turn is passed correctly.
-[x] Verify that the player names are updated correctly when the "playersName updated" event is triggered.
-[x] Verify that the game over message is displayed correctly when the "winner updated" event is triggered on all
 clients.
-[x] Verify that the game over message is displayed correctly when the "endGame" event is triggered on the host.

## Edge cases tests:

-[x] Try inserting an invalid IP address and port and verify that an error message is displayed.
-[x] Try placing a valid word bottom to top on the game board and verify that it doesn't get placed.
-[x] Try placing a valid word right to left on the game board and verify that it doesn't get placed.
-[x] Click on your keyboard on a tile which isn't in the hand and verify that it doesn't get selected.
-[x] Try starting a game without any players and verify that an error message is displayed.
-[x] Try starting a game with only one player and verify that everything works correctly.
-[x] Try starting a game with more than the maximum allowed number of players and verify that it blocks them from
 joining.
-[x] Try placing a word on the game board that exceeds the maximum allowed word length and verify that it doesn't get
 placed.
-[x] Try clicking on the game board outside the valid play area and verify that the tile selection is not affected.
-[x] Verify that the game handles invalid or unexpected events gracefully without crashing or displaying incorrect
 information.
-[x] Test the performance of the `GameWindowController` by simulating 4 players and game events to
 ensure it can handle the load efficiently.
-[x] Verify that the game gracefully handles network latency and delays in updating the game state on all clients.





