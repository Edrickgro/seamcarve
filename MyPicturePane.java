package seamcarve;

import javafx.scene.layout.BorderPane;
import support.seamcarve.*;


/**
 * This class is your seam carving picture pane.  It is a subclass of PicturePane,
 * an abstract class that takes care of all the drawing, displaying, carving, and
 * updating of seams and images for you.  Your job is to override the abstract
 * method of PicturePane that actually finds the lowest cost seam through
 * the image.
 *
 * See method comments and handouts for specifics on the steps of the seam carving algorithm.
 *
 *
 * @version 01/17/2019
 */

public class MyPicturePane extends PicturePane {
	private int[][] importance;
	private int[][] lowestCost;
	private int[][] directions;
	private int[] currSeam;

	/**
	 * The constructor accepts an image filename as a String and passes
	 * it to the superclass for displaying and manipulation.
	 *
	 * @param pane
	 * @param filename
	 */
	public MyPicturePane(BorderPane pane, String filename) {
		/** MyPicturePane: BorderPane, String -> None
		 Purpose: Constructor of the class that initializes the four arrays needed to create the seam
		 **/
		super(pane, filename);
		importance = new int[this.getPicHeight()][this.getPicWidth()];
		lowestCost = new int[this.getPicHeight()][this.getPicWidth()];
		directions = new int[this.getPicHeight()][this.getPicWidth()];
		currSeam = new int[this.getPicHeight()];

	}

	protected int[] findLowestCostSeam() {
		/** findLowestCostSeam: None -> int[][]
		 Produces: an array that illustrates the lowest cost seam in an image to delete and decrease size
		 **/

		/** First for loop that fills in the importance[][] array with importance values of every pixel **/
		for(int i = 0; i < this.getPicHeight(); i++){
			for(int j = 0; j < this.getPicWidth(); j++){
				importance[i][j] = this.getImportance(i,j); 
			}
		}

		/** Second for loop that starts from the bottom and fills the lowestCost[][] array with the lowest cost of every pixel succession **/
		for(int a = this.getPicHeight()-2; a>= 0; a--){
			for(int b = 0; b < this.getPicWidth(); b++){
				lowestCost[a][b] = this.getCost(a,b);
			}
		}

		/** Calls upon a method that fills in the currSeam[] array **/
		int lowestIndex = this.getIndex();
		this.fillSeam(lowestIndex);

		return currSeam;
	}


	public int getCost(int a, int b){
		/** getCost: coordinates of a pixel -> int minimum cost of specified pixel
		 Produces: an int indicating the minimum cost required to reach the pixel
		 **/
		int minimumValue;
		 if(a == this.getPicHeight()-1){
		 	return importance[a][b];
		 }
		 if((b-1)<0){

		 	minimumValue =  Math.min(lowestCost[a+1][b],lowestCost[a+1][b+1]) + importance[a][b];
		 	if(minimumValue == lowestCost[a+1][b]+importance[a][b]){
		 		directions[a][b]=0;
			}
		 	else{
		 		directions[a][b]=1;
			}
		 }
		 else if((b+1)==this.getPicWidth()){
		 	minimumValue = Math.min(lowestCost[a+1][b-1],lowestCost[a+1][b]) + importance[a][b];
		 	if(minimumValue == lowestCost[a+1][b-1]+importance[a][b]){
		 		directions[a][b] = -1;
			}
		 	else{
		 		directions[a][b] = 0;
			}

		 }
		 else{
		 	minimumValue =  Math.min(lowestCost[a+1][b-1],Math.min(lowestCost[a+1][b],lowestCost[a+1][b+1])) + importance[a][b];
		 	if(minimumValue == lowestCost[a+1][b-1]+importance[a][b]){
		 		directions[a][b] = -1;
			}
		 	else if(minimumValue == lowestCost[a+1][b]+importance[a][b]){
		 		directions[a][b] = 0;
			}
		 	else{
		 		directions[a][b] = 1;
			}
		 }

		return minimumValue;
	}



	public int getImportance(int i, int j){
		/** getImportance: pixel coordinates -> integer
		 Produces: calculate the value of given pixel in relation to its neighbors
		 **/
		int importanceValue = 0;
		int redValue = this.getColorRed(this.getPixelColor(i,j));
		int blueValue = this.getColorBlue(this.getPixelColor(i,j));
		int greenValue = this.getColorGreen(this.getPixelColor(i,j));

		if((i-1)>=0){
			importanceValue+= this.calculateImpValue(i-1,j,redValue,greenValue,blueValue);
		}
		if((i+1)<this.getPicHeight()){
			importanceValue+= this.calculateImpValue(i+1,j,redValue,greenValue,blueValue);
		}
		if((j-1)>=0){
			importanceValue+= this.calculateImpValue(i,j-1,redValue,greenValue,blueValue);
		}
		if((j+1)<this.getPicWidth()){
			importanceValue+= this.calculateImpValue(i,j+1,redValue,greenValue,blueValue);
		}
		return importanceValue;
	}


	public int calculateImpValue(int i, int j, int red, int green, int blue){
		/** calculateImpValue: pixel coordinates and its RGB values -> int
		 Purpose: the math portion of getImportance() that calculates the differences of the RGB values
		 **/
		int tempRed = this.getColorRed(this.getPixelColor(i,j));
		int tempBlue = this.getColorBlue(this.getPixelColor(i,j));
		int tempGreen = this.getColorGreen(this.getPixelColor(i,j));

		return Math.abs(tempRed - red) + Math.abs(tempGreen - green) + Math.abs(tempBlue - blue);
	}

	public int getIndex(){
		/** getIndex: None -> int
		 Produces: an integer indicating the column in which the first pixel of the lowest cost seam is
		 **/
		int lowest = 10000000;
		int index = 0;
		for(int j = 0; j < this.getPicWidth(); j++){
			if(lowestCost[0][j] < lowest){
				lowest = lowestCost[0][j];
				index = j;
			}
		}
		return index;
	}

	public void fillSeam(int index){
		/** fillSeam: int index -> None
		 Produces: fills in the currSeam[] array based on the directions[][] array
		 **/
		int tempIndex = index;
		currSeam[0] = index;
		for(int i = 0; i < this.getPicHeight() - 1; i++){
			if(directions[i][tempIndex] == -1){
				tempIndex = tempIndex - 1;
				currSeam[i+1] = tempIndex;
			}
			else if(directions[i][tempIndex] == 0){
				currSeam[i+1] = tempIndex;
			}
			else{
				tempIndex = tempIndex + 1;
				currSeam[i+1] = tempIndex;
			}

		}
	}

}