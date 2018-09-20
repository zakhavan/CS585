
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	// Name-constants to represent the seeds and cell contents

	public static final int PLAYER = 1;
	public static final int COMPUTER = 2;
	public static String playerLetter = "X";
	public static String computerLetter;
	public static String letter;
	static Random rand;

	// Name-constants to represent the various states of the game
	public static final int PLAYING = 0;

	// The game board and the game status

	public static ArrayList<String> board = new ArrayList<String>();

	public static int[] movesList = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	public static int move;
	public static int currentState; // the current state of the game
									// (PLAYING, DRAW, CROSS_WON, NOUGHT_WON)
	public static int currentPlayer; // the current player (CROSS or NOUGHT)
	public static int currntRow, currentCol; // current seed's row and column

	public static Scanner in = new Scanner(System.in); // the input Scanner
	public static Socket socket;

	/** The entry main method (the program starts here) */
	public static void main(String[] args) throws Exception {

		try {
			String host = "127.0.0.1";
			int port = 8007;
			InetAddress address = InetAddress.getByName(host);
			socket = new Socket(address, port);
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			for (int i = 0; i < 10; i++) {
				board.add("-");
			}
			playerGoesFirst();
			int move = chooseRandomMoveFromList(board, movesList);
			board = makeMove(board, letter, move);
			board = prettify(board);
			String sentBoard = "";
			for (String s : board) {
				sentBoard += s + ""; /// make sure
			}
			sentBoard += System.lineSeparator();
			bw.write(sentBoard);
			// bw.
			bw.flush();
			
			
			System.out.println("initial board sent to the server : " + sentBoard);

			// ArrayList<String> board1 = (ArrayList<String>) board.subList(0, move);
			// ArrayList<String> board1 = new ArrayList<String>(board.subList(0, move));

			System.out.println("board is: " + board + "the board length is: " + board.size());

			while (true) {

				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String bo = br.readLine();
				char[] array = bo.toCharArray();
				ArrayList<String> board = new ArrayList<String>();
				for(char c:array) {
					  board.add(String.valueOf(c));
					}
				
				System.out.println("board received from Server is " + board);
				if (board.equals(null))
					break;

				if (board.contains("tied") || board.contains("client won") || board.contains("server won"))
					break;
				board = uglify(board);
				if (hasWon(board, "O")) {
					System.out.println(prettify(board) + " -> Server won.");
					bw.write("server won"+System.lineSeparator());
					bw.flush();
					break;

				} else if (isBoardFull(board)) {
					System.out.println(prettify(board) + " -> Game tied.");
					bw.write("tied"+System.lineSeparator());
					bw.flush();
					break;
				}
				move = getComputerMove(board, "X");
				board = makeMove(board, letter, move);


				System.out.println(prettify(board) + "Played with X on " + move);
				
				
				if (hasWon(board, "X")) {
					System.out.println(prettify(board) + " -> Client won.");
					bw.write("client won" + System.lineSeparator());
					bw.flush();
					break;

				} else if (isBoardFull(board)) {
					System.out.println(prettify(board) + " -> Game tied.");
					bw.write("tied"+ System.lineSeparator());
					bw.flush();
					break;
				}
				board = prettify(board);
				sentBoard = "";
				for (String s : board) {
					sentBoard += s + ""; /// make sure
				}
				sentBoard += System.lineSeparator();
				bw.write(sentBoard);
				// bw.
				bw.flush();
				
				
				System.out.println("board sent to the server : " + sentBoard);
				/*printBoard();
				playerGoesFirst();
				makeMove(board, letter, move);
				hasWon(board, letter);
				getBoardCopy(board);
				isSpaceFree(board, move);
				getPlayerMove(board, movesList);
				isBoardFull(board);
				prettify(board);
				uglify(board);
				chooseRandomMoveFromList(board, movesList);
				getComputerMove(board, computerLetter);
				playAgain();*/

			}
		}

		finally {

			try {
				socket.close();
			} catch (IOException e) {
			}
		}

		// killServer(host, port);////where should I call this function?

	}

	// This function prints out the board.

	public static void printBoard() {
		System.out.println("   |   |");
		System.out.println(' ' + board.get(7) + " | " + board.get(8) + " | " + board.get(9));
		System.out.println("   |   |");
		System.out.println("-----------");
		System.out.println("   |   |");
		System.out.println(" " + board.get(4) + " | " + board.get(5) + " | " + board.get(6));
		System.out.println("   |   |");
		System.out.println("-----------");
		System.out.println("   |   |");
		System.out.println(" " + board.get(1) + " | " + board.get(2) + " | " + board.get(3));
		System.out.println("   |   |");

	}

	// Player will go first
	public static int playerGoesFirst() {
		letter = playerLetter;
		currentPlayer = PLAYER;
		return currentPlayer;

	}

	// This function returns True if the player wants to play again, otherwise it
	// returns False.
	public static boolean playAgain() {
		System.out.print("Play again (y/n)? ");
		char ans = in.next().charAt(0);
		if (ans != 'y') {
			return false;
		} else {
			return true;
		}
	}

	public static ArrayList<String> makeMove(ArrayList<String> board, String letter, int move) {
		ArrayList<String> updateBoard = new ArrayList<String>(board.subList(0, move));
		updateBoard.add(letter);
		updateBoard.addAll(board.subList(move + 1, 10));
		return updateBoard;

	}

	public static boolean hasWon(ArrayList<String> bo, String le) {
		return ((bo.get(7).equals(le) && bo.get(8).equals(le) && bo.get(9).equals(le))
				|| (bo.get(4).equals(le) && bo.get(5).equals(le) && bo.get(6).equals(le))
				|| (bo.get(1).equals(le) && bo.get(2).equals(le) && bo.get(3).equals(le))
				|| (bo.get(7).equals(le) && bo.get(4).equals(le) && bo.get(1).equals(le))
				|| (bo.get(8).equals(le) && bo.get(5).equals(le) && bo.get(2).equals(le))
				|| (bo.get(9).equals(le) && bo.get(6).equals(le) && bo.get(3).equals(le))
				|| (bo.get(7).equals(le) && bo.get(5).equals(le) && bo.get(3).equals(le))
				|| (bo.get(9).equals(le) && bo.get(5).equals(le) && bo.get(1).equals(le)));

	}

	public static ArrayList<String> getBoardCopy(ArrayList<String> board) {
		return board;

	}

	public static boolean isSpaceFree(ArrayList<String> board, int move) {
		return board.get(move).equals("-");

	}

	public static int getPlayerMove(ArrayList<String> board, int[] movesList) {
		String move1 = "-";
		do {
			System.out.println("What is your next move? (1-9)");
			move1 = in.next();
			return Integer.parseInt(move1);
		} while (isSpaceFree(board, Integer.parseInt(move1)) == false
				|| Arrays.asList(movesList).contains(move1) == false);
	}

	public static boolean isBoardFull(ArrayList<String> board) {
		for (int i = 1; i < 10; i++) {
			if (isSpaceFree(board, i))
				return false;

		}
		return true;
	}

	public static ArrayList<String> prettify(ArrayList<String> board) {

		ArrayList<String> prettyBoard = new ArrayList<String>(board.subList(0, 4));
		prettyBoard.add("|");
		prettyBoard.addAll(board.subList(4, 7));
		prettyBoard.add("|");
		prettyBoard.addAll(board.subList(7, 10));
		return prettyBoard;

	}

	public static ArrayList<String> uglify(ArrayList<String> board) {
		ArrayList<String> board1 = new ArrayList<String>();
		for (int i = 0; i < board.size(); i++) {
			if(board.get(i).equals("|")==false) {
			board1.add(board.get(i));
		}
		}
		return board1;
	}

	public static java.lang.Integer chooseRandomMoveFromList(ArrayList<String> board, int[] movesList) {

		ArrayList<Integer> possibleMoves = new ArrayList<Integer>();
		for (int i = 1; i < 10; i++) {
			if (isSpaceFree(board, i))
				possibleMoves.add(i);
		}
		if (possibleMoves.size() != 0) {
			rand = new Random();
			int random = possibleMoves.get(rand.nextInt(possibleMoves.size()));
			System.out.print(random);
			return random;
		} else
			return null;
	}

	public static int getComputerMove(ArrayList<String> board, String computerLetter) {
		if (computerLetter.equals("X"))
			playerLetter = "O";
		else
			playerLetter = "X";
		for (int i = 1; i < 10; i++) {

			ArrayList<String> copy = getBoardCopy(board);
			if (isSpaceFree(copy, i)) {
				copy = makeMove(copy, playerLetter, i);
				if (hasWon(copy, playerLetter))
					return i;
			}
		}
       //return chooseRandomMoveFromList(board, movesList);


		 return chooseRandomMoveFromList(board, movesList);
		/*int[] cornerMoves = { 1, 3, 7, 9 };
		int move = chooseRandomMoveFromList(board, cornerMoves);
		if (move != 0) // make sure
			return move;
		if (isSpaceFree(board, 5))
			return 5;
		else {
			int[] sideMoves = { 2, 4, 6, 8 };
			return chooseRandomMoveFromList(board, sideMoves);*/
		}

	}


