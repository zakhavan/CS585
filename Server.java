import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
   // Name-constants to represent the seeds and cell contents
 
   
   public static final int COMPUTER = 2;
   public static String playerLetter = "X";
   public static String computerLetter;
   public static String letter;
   static  Random rand;
   // The game board and the game status
  
   public static ArrayList<String> board = new ArrayList<String>(); 
                                                      
   public static int[] movesList = {1, 2, 3, 4, 5, 6, 7, 8, 9};
   public static int move;
  
 
   public static Scanner in = new Scanner(System.in); // the input Scanner
   public static Socket socket;
 
   /** The entry main method (the program starts here) */
   public static void main(String[] args) throws Exception {
	   ServerSocket listener = new ServerSocket(8007);
       System.out.println("Server is Running");	   
       try {
           while (true) {
        	   System.out.println("Waiting for clients :");
        	   socket = listener.accept();
        	   InputStream is = socket.getInputStream();
               InputStreamReader isr = new InputStreamReader(is);
               BufferedReader br = new BufferedReader(isr);
               System.out.println("Client connected :");
        	   while(true)
        	   {
        		   
	               String bo = br.readLine();
	               if(bo.equals(null))
	            	   break;
	               
	               if(bo.contains("tied") || bo.contains("client won") || bo.contains("server won"))
	            	   break;
	               char[] array = bo.toCharArray();
					ArrayList<String> board = new ArrayList<String>();
					for(char c:array) {
						  board.add(String.valueOf(c));
						}
					
	              
	               //ArrayList<String> board = new ArrayList<String>();
	               System.out.println("board received from client is "+board);
	               
	               
	        
	               board = uglify(board);
	               
	               int move = getComputerMove(board, "O");
	               board = makeMove(board, "O", move);
	               System.out.println(prettify(board)+" -> Played with O on "+ move);


	               board = prettify(board);
	               
	               OutputStream os = socket.getOutputStream();
	               OutputStreamWriter osw = new OutputStreamWriter(os);
	               BufferedWriter bw = new BufferedWriter(osw);
	               String sentBoard = "";
	               for (String s : board)
	               {
	                   sentBoard += s + ""; ///make sure
	               }
					sentBoard += System.lineSeparator();
				 //TimeUnit.SECONDS.sleep(5);
	               bw.write(sentBoard);
	               bw.flush();
	               System.out.println("board sent to the client : "+sentBoard);
	               
	              /* printBoard();
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
           
       
       
       
     
	   
		   
		   
       }
       finally {
    	   listener.close();
           try {socket.close();} catch (IOException e) {}
       }
	   
	   
	   }

   
   //This function prints out the board.
   
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
   
   //Player will go first 
   public static void playerGoesFirst() {
	   letter = playerLetter;
	   
	   
   }
   
   //This function returns True if the player wants to play again, otherwise it returns False.
   public static boolean playAgain() {
	   System.out.print("Play again (y/n)? ");
	   char ans = in.next().charAt(0);
	   if (ans != 'y') {
		   return false;
		   }
	   else {
		   return true;
		   }
   }
   
   
   public  static ArrayList<String> makeMove(ArrayList<String> board, String letter, int move) {
	   ArrayList<String> updateBoard = new ArrayList<String>(board.subList(0, move));
	   updateBoard.add(letter);
	   updateBoard.addAll(board.subList(move+1, 10));
	   return updateBoard;
	   
	   
   }
   
   
   public static boolean hasWon(ArrayList<String> bo, String le) {
	   return ((bo.get(7).equals(le) && bo.get(8).equals(le) && bo.get(9).equals(le)) || 
			    (bo.get(4).equals(le) && bo.get(5).equals(le) && bo.get(6).equals(le)) || 
			    (bo.get(1).equals(le) && bo.get(2).equals(le) && bo.get(3).equals(le)) || 
			    (bo.get(7).equals(le) && bo.get(4).equals(le) && bo.get(1).equals(le)) || 
			    (bo.get(8).equals(le) && bo.get(5).equals(le) && bo.get(2).equals(le)) || 
			    (bo.get(9).equals(le) && bo.get(6) .equals(le) && bo.get(3).equals(le)) || 
			    (bo.get(7).equals(le) && bo.get(5).equals(le) && bo.get(3).equals(le)) || 
			    (bo.get(9).equals(le) && bo.get(5).equals(le) && bo.get(1).equals(le)));
	   
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
	   }while(isSpaceFree(board, Integer.parseInt(move1))== false || Arrays.asList(movesList).contains(move1) == false);
   }
   
   public static boolean isBoardFull(ArrayList<String> board) {
	   for(int i = 0; i < 10; i++) {
		   if(isSpaceFree(board,i)) return false;
		  
			   
		   
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
	   for(int i = 1; i < 10; i++) {
		   if(isSpaceFree(board, i))
			   possibleMoves.add(i);
	   }
	   if(possibleMoves.size() != 0) {
		   rand = new Random();
	   	   int random = possibleMoves.get(rand.nextInt(possibleMoves.size()));
	   	   System.out.println(random);
	   	   return random;
	   }
	   else return null;
   }
   
   public static int getComputerMove(ArrayList<String> board, String computerLetter) {
	   if(computerLetter.equals("X")) 
		   playerLetter = "O";
	   else 
		   playerLetter = "X";
	   for(int i = 1; i <10; i++) {
		   
		   ArrayList<String> copy = getBoardCopy(board);
		   if(isSpaceFree(copy, i)) {
			   copy = makeMove(copy, playerLetter, i);
			   if(hasWon(copy, playerLetter))
				   return i;
		   }
		  // return chooseRandomMoveFromList(board, movesList);
	   }
	   
	   return chooseRandomMoveFromList(board, movesList);
	   /*int[] cornerMoves = {1, 3, 7, 9};
	   int move = chooseRandomMoveFromList(board, cornerMoves);
	   if(move != 0)       //make sure
		   return move;
	   if(isSpaceFree(board, 5))
		   return 5;
	   else {
		  int[]  sideMoves = {2, 4, 6, 8};
		  return chooseRandomMoveFromList(board, sideMoves);*/
		  }
	   }
   
