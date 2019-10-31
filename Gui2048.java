/** Gui2048.java */
/** PA8 Release */
/* ////////////////////////////////////////////////////////////////////////////
 * Creator:      Garner Deng A98025363
 * CSE8B ID:     cse8bhd
 * Date Created: 5-28-2015
 * File Name:    Gui20148.java
 * Sources:      Tutor advice, the internet.
 * ////////////////////////////////////////////////////////////////////////////
 * Description: The following code will play the popular game known as "2048". 
 *              Upon compiling the program with "javac Gui2048.java" and
 *              running the program with "java Gui2048", a window with the 
 *              title "Gui2048" will open. In the window, the arrow keys will
 *              have an effect on the tiles. When a move is successful, a 
 *              record of the move direction will appear in the terminal, as
 *              well as the current score. This is for troubleshooting. If a
 *              move resulted in the creation of a new, higher value tile, the
 *              score on the GUI will be updated by adding the value of the new
 *              tile. When an inputted move is invalid, the terminal will print
 *              a message indicating that the user input is invalid. This does
 *              not interrupt the program; the program will continue running
 *              until the game is lost, at which a "Game Over" overlay will
 *              appear over the screen. There is also a "S" input key that 
 *              will save the game to a file named SaveFiLE.
 */////////////////////////////////////////////////////////////////////////////

import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import java.util.*;
import java.io.*;
import javafx.scene.input.KeyCode;

///////////////////////////////////////////////////////////////////////////////
//
//The Main Class
public class Gui2048 extends Application
{
  private String outputBoard; // The filename for where to save the Board
  private Board board;        // The 2048 Game Board

  // Fill colors for each of the Tile values
  private static final Color COLOR_EMPTY = Color.rgb(238, 228, 218, 0.35);
  private static final Color COLOR_2 = Color.rgb(238, 228, 218);
  private static final Color COLOR_4 = Color.rgb(237, 224, 200);
  private static final Color COLOR_8 = Color.rgb(242, 177, 121);
  private static final Color COLOR_16 = Color.rgb(245, 149, 99);
  private static final Color COLOR_32 = Color.rgb(246, 124, 95);
  private static final Color COLOR_64 = Color.rgb(246, 94, 59);
  private static final Color COLOR_128 = Color.rgb(237, 207, 114);
  private static final Color COLOR_256 = Color.rgb(237, 204, 97);
  private static final Color COLOR_512 = Color.rgb(237, 200, 80);
  private static final Color COLOR_1024 = Color.rgb(237, 197, 63);
  private static final Color COLOR_2048 = Color.rgb(237, 194, 46);
  private static final Color COLOR_OTHER = Color.BLACK;
  private static final Color COLOR_GAME_OVER = Color.rgb(238,228,218,0.73);

  private static final Color COLOR_VALUE_LIGHT = Color.rgb(249, 246, 242);
  // For tiles >= 8
  private static final Color COLOR_VALUE_DARK = Color.rgb(119, 110, 101);
  // For tiles < 8

  //Instance Variables 
  public int TileValue;       //Face value of the Tile. Zero won't dispay.
  public int GRID_SIZE;       //The grid length.  
  public int score;           //Stores the score. 
  public Text ScoreText;      //The Text that will display the score.
  public Tile[][] TileGrid;   //A square grid array that holds Tile elements
  public Tile GameOverTile;   //A tile that displays when the game is over. 

  @Override //Override the start method in the Application class
    public void start(Stage primaryStage){

      // Process Arguments and Initialize the Game Board
      processArgs(getParameters().getRaw().toArray(new String[0]));

      //Initializing Board and SaveFiLE
      this.board = new Board(8,new Random());
      this.outputBoard = "SaveFiLE";
      try{
        board.saveBoard(outputBoard);
      }catch(IOException a){
        System.out.println("saveBoard threw an Exception");
      }

      //Create a BorderPane and a horizontal box.
      BorderPane pane = new BorderPane();
      HBox hbox = new HBox();
      hbox.setPadding(new Insets(15,12,15,12));
      hbox.setSpacing(10);
      hbox.setStyle("-fx-background-color: rgb(0,255,255)");

      //Creating Text Element for title, "2048"
      Text aText = new Text();
      aText.setText("2048");
      aText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
      aText.setFill(Color.BLACK);

      //Creating Text Element for score, "Score: ___"
      this.ScoreText = new Text();
      ScoreText.setText("Score: " + board.getScore());
      ScoreText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
      ScoreText.setFill(Color.BLACK);

      //Adds the newly created 2048 and Score texts to the horizontal box.
      hbox.getChildren().addAll(aText, ScoreText);

      //Creates a GridPane for the game board.
      GridPane gridpane = new GridPane();
      gridpane.setAlignment(Pos.CENTER);
      gridpane.setPadding(new Insets(11.5,12.5,13.5,14.5));
      gridpane.setHgap(5.5);
      gridpane.setVgap(5.5);
      gridpane.setStyle("-fx-background-color: rgb(119,110,100)");

      //Adds the horizontal box and GridPane to the BorderPane.
      pane.setTop(hbox);
      pane.setCenter(gridpane);
      pane.setStyle("-fx-background-color: rgb(119, 110, 100)");

      //Grabbing the Grid length and score value from the board. 
      this.GRID_SIZE = board.getGRID_SIZE();
      this.score = board.getScore();

      //Creates all the Tiles and adds them to the GridPane
      //The TileGrid is a new tile grid array that perfectly mirrors
      //the contents of the int grid array in board.java. 
      //Each element of TileGrid will have a Tile in it. A Tile is a 
      //special class that has numerous elements that will be fully
      //addressed in the Tile code. After the tiles are created, they
      //are "added" to the GridPane and then centered for aesthetic 
      //purposes. 
      this.TileGrid = new Tile[GRID_SIZE][GRID_SIZE];
      for(int row = 0; row < GRID_SIZE; row++){
        for(int column = 0; column < GRID_SIZE; column++){
          TileValue = board.getGridNumber(row,column);
          Tile ThisTile = new Tile(TileValue);
          gridpane.add(ThisTile.getRectangle(), column, row + 1);
          gridpane.add(ThisTile.getText(), column, row + 1);
          GridPane.setHalignment(ThisTile.getText(), HPos.CENTER);
          this.TileGrid[row][column] = ThisTile;
        }
      }

      //Creates the GameOverTile and makes it invisible. It will display 
      //when the game is lost. The GameOverTile is a Tile that is much 
      //larger than the other Tiles used as nodes in GridPane.
      //Specifically, its dimensions are calculated by getting the combined
      //length of all the Tiles in one row/column, and adding it to the 
      //preset gaps between nodes. This calculation causes the GameOverTile
      //to perfectly cover the grid area of GridPane. When the game is lost,
      //the text color and rectangle fill color of the GameOverTile are made
      //visible. In essence, the game was lost before it even began. 
      this.GameOverTile = new Tile(0);
      int SIZE=(int)(GameOverTile.oneSquare.getHeight()+gridpane.getHgap());
      SIZE = SIZE * GRID_SIZE;
      this.GameOverTile.oneSquare.setHeight(SIZE);
      this.GameOverTile.oneSquare.setWidth(SIZE);
      this.GameOverTile.oneSquare.setFill(Color.rgb(255,255,255,0));
      this.GameOverTile.tileText.setFill(Color.rgb(255,255,255,0));
      this.GameOverTile.tileText.setText("Game Over!!");

      GridPane.setHalignment(GameOverTile.getText(),HPos.CENTER);
      gridpane.add(GameOverTile.getRectangle(),0,0,GRID_SIZE,GRID_SIZE+1);
      gridpane.add(GameOverTile.getText(),0,0,GRID_SIZE,GRID_SIZE+1);

      //Creating a scene and placing it in the Stage
      Scene scene = new Scene(pane);
      primaryStage.setTitle("Gui2048");   //Sets stage title
      primaryStage.setScene(scene);       //Places the scene in the stage
      primaryStage.show();                //Display the stage

      //Instantiation of key handler 
      //This will allow program to read key inputs from the user.
      //It will only accept the arrow keys, and S for save.
      scene.setOnKeyPressed(new myKeyHandler());
    }

  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////

  //Methods

  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  //Tile Class. Tiles are composed of a colored rectangle(actually a square),
  //colored text, and a number. The colored text is the number. 
  public class Tile extends Rectangle{

    private Rectangle oneSquare;
    private Color tileColor1;
    private int tileValue;
    private String tileName;
    private Text tileText;

    //Creates a new Tile. Requires a parameter int tileValue. tileValue is 
    //the face value of the tile, such as "2" or "4", etc. This method
    //creates the tile based on the value of tileValue; tileValue determines
    //the color of the rectangle, the color of the text, and the text itself. 
    public Tile(int tileValue){ 

      //Creates the Rectangle, finds the appropriate color based on tileValue
      this.oneSquare = new Rectangle();
      this.oneSquare.setWidth(100);
      this.oneSquare.setHeight(100);
      this.tileColor1 = getColor(tileValue); 
      this.oneSquare.setFill(tileColor1);

      //Creates the Text, converts tileValue to a string for the text. 
      this.tileValue = tileValue;
      this.tileName = String.valueOf(tileValue);
      this.tileText = new Text();

      //Sets the text. If the tileValue is 0, no text will be displayed. 
      tileText.setText(tileName);
      if(tileValue==0)
        tileText.setText("");
      tileText.setFont(Font.font("Times New Roman", FontWeight.BOLD,30));

      //Sets the color of the text. As the game progresses, tiles increase
      //in value. Higher value tiles grow darker in color, while lower value
      //tiles are light in color. So, the text color of the tile is dark when 
      //the tile is <8 and the text color is light otherwise. 
      if(tileValue >= 8)
        this.tileText.setFill(COLOR_VALUE_LIGHT);
      else   
        this.tileText.setFill(COLOR_VALUE_DARK);
    }



    //Changes information on a tile. This is called whenever a user 
    //inputted arrowkey successfully moves a tile. This method takes
    //the face value (TileValue1) and uses it as a parameter for 
    //appropriately modifying the old tile. 
    public void changeTile(int TileValue1){

      this.tileValue = TileValue1;
      this.tileColor1 = getColor(TileValue1);
      this.tileName = String.valueOf(TileValue1);
      this.tileText.setText(tileName);

      if(tileValue==0)
        tileText.setText("");
      if(tileValue >= 8)
        this.tileText.setFill(COLOR_VALUE_LIGHT);
      else   
        this.tileText.setFill(COLOR_VALUE_DARK);
      this.oneSquare.setFill(tileColor1);
    }



    //Gets the color of the tile, based on the face value TileValue2. 
    public Color getColor(int TileValue2){
      Color tileColor = COLOR_EMPTY;
      switch(TileValue2){
        case 0:    tileColor = COLOR_EMPTY;
                   break;
        case 2:    tileColor = COLOR_2;
                   break;
        case 4:    tileColor = COLOR_4;
                   break;
        case 8:    tileColor = COLOR_8;
                   break;
        case 16:   tileColor = COLOR_16;
                   break;
        case 32:   tileColor = COLOR_32;
                   break;
        case 64:   tileColor = COLOR_64;
                   break;
        case 128:  tileColor = COLOR_128;
                   break;
        case 256:  tileColor = COLOR_256;
                   break;
        case 512:  tileColor = COLOR_512;
                   break;
        case 1024: tileColor = COLOR_1024;
                   break;
        case 2048: tileColor = COLOR_2048;
                   break;
      }
      return tileColor;
    }


    //returns the rectangle of a tile for node adding purposes.
    public Rectangle getRectangle(){
      return this.oneSquare;
    }

    //returns the text element of a tile for node adding purposes.
    public Text getText(){
      return this.tileText;
    }
  }


  /////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////


  //Key Handler. Handles the user inputted keys and performs the operations. 
  private class myKeyHandler implements EventHandler<KeyEvent>{

    public int TileValue;
    public Text text;
    public boolean InputWasNotS;
    @Override
      public void handle(KeyEvent e){

        //This determines what specifically the input is; which arrow key, or
        //if the input is S for save. If the input is S, the board saves.
        //If the input is an arrow key, the direction of the arrow key is 
        //stored and used for the next piece of code, which will move the 
        //board in the direction of the arrow key.
        try{

          KeyCode key = e.getCode();
          Direction thisway = Direction.UP;

          switch(e.getCode()){
            case UP:    thisway = Direction.UP;
                        InputWasNotS = true;
                        break;
            case DOWN:  thisway = Direction.DOWN;
                        InputWasNotS = true;
                        break;
            case LEFT:  thisway = Direction.LEFT;
                        InputWasNotS = true;
                        break;
            case RIGHT: thisway = Direction.RIGHT;
                        InputWasNotS = true;
                        break;
            case S:     InputWasNotS = false;
                        board.saveBoard(outputBoard);
                        System.out.println("Saving Board to" + outputBoard);
                        break;
          }

          //Checks if the inputted arrow key is a possible move. This
          //prints a message "You cannot move in that direction" if that
          //direction is possible, and the game is not yet over. It
          //prints to the terminal. 
          if(board.canMove(thisway)==false&&board.isGameOver()==false)
            if(InputWasNotS){
              System.out.println("You cannot move in that direction");
            }
          //Runs if the move is possible. It will move the board from
          //board.java, print the direction of the move into the terminal,
          //prints the updated score into the terminal for troubleshooting
          //purposes, and then updates the score display on the GUI. 
          //Then it adds a random tile.
          if(board.canMove(thisway)&&InputWasNotS){
            board.move(thisway);
            System.out.println("Moving" + " " + thisway);
            System.out.println("Score " + board.getScore());
            ScoreText.setText("Score: " + board.getScore());
            board.addRandomTile();

            //Updates all the tiles. 
            for(int row = 0; row < GRID_SIZE; row++){
              for(int column = 0; column < GRID_SIZE; column++){
                TileValue = 0;
                TileValue = board.getGridNumber(row,column);
                TileGrid[row][column].changeTile(TileValue);
                text = TileGrid[row][column].getText();
                GridPane.setHalignment(text,HPos.CENTER);

                //After all is done and updated, the game checks if the game is
                //over. If it is, it will make the GameOverTile visible. 
                if(board.isGameOver()){
                  GameOverTile.oneSquare.setFill(Color.rgb(238,228,218,0.73)); 
                  GameOverTile.tileText.setFill(Color.BLACK);
                }
              }
            }
          }
        }
        catch (IOException ex){
          System.out.println("Invalid Input");
          ex.printStackTrace();
        }
      }
  }
  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////
  /** DO NOT EDIT BELOW */

  // The method used to process the command line arguments
  private void processArgs(String[] args)
  {
    String inputBoard = null;   // The filename for where to load the Board
    int boardSize = 0;          // The Size of the Board

    // Arguments must come in pairs
    if((args.length % 2) != 0)
    {
      printUsage();
      System.exit(-1);
    }

    // Process all the arguments 
    for(int i = 0; i < args.length; i += 2)
    {
      if(args[i].equals("-i"))
      {   // We are processing the argument that specifies
        // the input file to be used to set the board
        inputBoard = args[i + 1];
      }
      else if(args[i].equals("-o"))
      {   // We are processing the argument that specifies
        // the output file to be used to save the board
        outputBoard = args[i + 1];
      }
      else if(args[i].equals("-s"))
      {   // We are processing the argument that specifies
        // the size of the Board
        boardSize = Integer.parseInt(args[i + 1]);
      }
      else
      {   // Incorrect Argument 
        printUsage();
        System.exit(-1);
      }
    }

    // Set the default output file if none specified
    if(outputBoard == null)
      outputBoard = "2048.board";
    // Set the default Board size if none specified or less than 2
    if(boardSize < 2)
      boardSize = 4;

    // Initialize the Game Board
    try{
      if(inputBoard != null)
        board = new Board(inputBoard, new Random());
      else
        board = new Board(boardSize, new Random());
    }
    catch (Exception e)
    {
      System.out.println(e.getClass().getName() + " was thrown while creating a " +
          "Board from file " + inputBoard);
      System.out.println("Either your Board(String, Random) " +
          "Constructor is broken or the file isn't " +
          "formated correctly");
      System.exit(-1);
    }
  }

  // Print the Usage Message 
  private static void printUsage()
  {
    System.out.println("Gui2048");
    System.out.println("Usage:  Gui2048 [-i|o file ...]");
    System.out.println();
    System.out.println("  Command line arguments come in pairs of the form: <command> <argument>");
    System.out.println();
    System.out.println("  -i [file]  -> Specifies a 2048 board that should be loaded");
    System.out.println();
    System.out.println("  -o [file]  -> Specifies a file that should be used to save the 2048 board");
    System.out.println("                If none specified then the default \"2048.board\" file will be used");
    System.out.println("  -s [size]  -> Specifies the size of the 2048 board if an input file hasn't been");
    System.out.println("                specified.  If both -s and -i are used, then the size of the board");
    System.out.println("                will be determined by the input file. The default size is 4.");
  }
}
