package edu.lclark.drosophila;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;

public class Analyzer {

	private int sizeThreshold;

	private static AnalyzerGui gui;

	/**
	 * The threshold in pixel color contrast a pixel must pass to be identified.
	 */
	private static final int CONTRAST_THRESHOLD = 200;

	public static void main(String[] args) {
		gui = new AnalyzerGui(new Analyzer());
		gui.run();
	}

	/**
	 * The List of Fly objects in which fly data is stored.
	 */
	private List<Fly> flies;

	/**
	 * The total number of frames in the movie being analyzed. Or, the number of
	 * images being analyzed.
	 */
	private int totalFrames;

	/**
	 * True if the currently loaded file is a movie. Otherwise, it is false.
	 * <p>
	 * This is used so that the proper constructor is called for making a new
	 * Fly at the end of Flydentify.
	 */
	private boolean movieLoaded;

	/**
	 * Stores all of the images being analyzed.
	 */
	private File[] images;

	public Analyzer() {
		movieLoaded = false;
		totalFrames = 0;
		flies = new LinkedList<Fly>();
		images = new File[20];
	}

	/**
	 * Calculates the mean velocity of the given fly within the time specified
	 * by the starting and ending frames.
	 * 
	 * @param fly
	 *            the fly whose average velocity is desired.
	 * @param start
	 *            the first frame you want the average velocity calculated from.
	 * @param end
	 *            the last frame you want the average velocity calculated from.
	 * @return the mean velocity of the given fly within the given time frame.
	 */
	public double averageVelFly(Fly fly, int start, int end) {
		double avgVel = 0;
		double[] vx = fly.getVx();
		double[] vy = fly.getVy();
		for (int i = start; i <= end; i++) {
			avgVel += vx[i] + vy[i];
		}
		avgVel = avgVel / (end - (start - 1));
		return avgVel;
	}

	/**
	 * Identifies any flies within the given image and adds to the information
	 * within the flies List for the given frame. In order to retrieve
	 * information, use the flies List in this Analyzer, through getFlies().
	 * 
	 * @param image
	 *            the image which is being analyzed.
	 * @param frameNumber
	 *            which frame out of all frames the given image is.
	 */
	public void flydentify(BufferedImage image, int frameNumber) {
		if (frameNumber == 0) {
			flies = new LinkedList<Fly>();
		}
		int imgHeight = image.getHeight();
		int imgWidth = image.getWidth();
		// a temporary array of the flies found in this image. It just stores
		// their x and y
		LinkedList<double[]> tempFlies = new LinkedList<double[]>();
		// an array of which pixels have been searched
		boolean searchArray[][] = new boolean[imgWidth][imgHeight];
		int stack[][] = new int[imgWidth * imgHeight][2];
		int curIdx;
		for (int i = 0; i < imgWidth; i++) {
			for (int j = 0; j < imgHeight; j++) {
				if (!searchArray[i][j]) { // if pixel hasn't been searched
					// find gray scale value of the pixel. TODO we aren't
					// completely sure if this works
					int rgb = image.getRGB(i, j);
					int red = (rgb >> 16) & 0xFF;
					int green = (rgb >> 8) & 0xFF;
					int blue = rgb & 0xFF;
					double avg = red * 0.2989 + green * .587 + blue * .114;
					if ((int) (Math.round(avg)) <= CONTRAST_THRESHOLD) { // if
																			// the
																			// color
																			// is
																			// dark
																			// enough
						int totalX = 0;
						int totalY = 0;
						int numPixels = 0; // initialize values to find center
											// of
											// mass of fly
						// searchPixel(i, j, searchArray, image);
						curIdx = 0;
						stack[curIdx][0] = i;
						stack[curIdx][1] = j;
						curIdx++;
						while (curIdx > 0) {
							curIdx--;
							int tempx = stack[curIdx][0];
							int tempy = stack[curIdx][1];
							searchArray[tempx][tempy] = true;
							totalX += tempx;
							totalY += tempy;
							numPixels++;
							if ((tempx > 0) && !searchArray[tempx - 1][tempy]) {
								if (isDarkEnough(image.getRGB(tempx - 1, tempy))) {
									stack[curIdx][0] = tempx - 1;
									stack[curIdx][1] = tempy;
									curIdx++;
								}
							}
							if ((tempy > 0) && !searchArray[tempx][tempy - 1]) {
								if (isDarkEnough(image.getRGB(tempx, tempy - 1))) {
									stack[curIdx][0] = tempx;
									stack[curIdx][1] = tempy - 1;
									curIdx++;
								}
							}
							if ((tempx < imgWidth - 1)
									&& !searchArray[tempx + 1][tempy]) {
								if (isDarkEnough(image.getRGB(tempx + 1, tempy))) {
									stack[curIdx][0] = tempx + 1;
									stack[curIdx][1] = tempy;
									curIdx++;
								}
							}
							if ((tempy < imgHeight - 1)
									&& !searchArray[tempx][tempy + 1]) {
								if (isDarkEnough(image.getRGB(tempx, tempy + 1))) {
									stack[curIdx][0] = tempx;
									stack[curIdx][1] = tempy + 1;
									curIdx++;
								}
							}
						}
						if (numPixels >= sizeThreshold) // if the blob is large
														// enough to be a fly
						{
							// create a new temporary fly object
							double tempLocation[] = new double[2];
							tempLocation[0] = (double) totalX / numPixels;
							tempLocation[1] = (double) totalY / numPixels;
							tempFlies.add(tempLocation);
							//System.out.println("size: " + numPixels);
						}
					} else {
						// we searched this already!
						searchArray[i][j] = true;
					}
				}
			}
		}
		// create this variable because flies.size() is linear
		int sizeFlies = flies.size();
		for (int i = 0; i < sizeFlies && !tempFlies.isEmpty(); i++) {
			// going through each existing fly and matching it to the closest
			// temporary counterpart
			Fly pastFly = flies.get(i);
			double pastX = pastFly.getX(0);
			double pastY = pastFly.getY(0);
			double dist = Math.sqrt(Math.pow(pastX - tempFlies.get(0)[0], 2)
					+ Math.pow(pastY - tempFlies.get(0)[1], 2));
			int closestFlyIndex = 0;
			int sizeTempFlies = tempFlies.size();
			for (int j = 1; j < sizeTempFlies; j++) {
				double thisDist = Math.sqrt(Math.pow(pastX
						- tempFlies.get(j)[0], 2)
						+ Math.pow(pastY - tempFlies.get(j)[1], 2));
				if (thisDist < dist) {
					dist = thisDist;
					closestFlyIndex = j;
				}
			}
			// augmenting the existing fly to contain information about location
			// from the image
			pastFly.addFrameInfo(frameNumber,
					tempFlies.get(closestFlyIndex)[0],
					tempFlies.get(closestFlyIndex)[1]);
			// remove the temporary fly so two existing flies can't map to the
			// same temporary one
			tempFlies.remove(closestFlyIndex);
		}
		// in case there are more temporary flies than existing flies
		sizeFlies = tempFlies.size();
		while (!tempFlies.isEmpty()) {
			// create a new fly for each fly left
			Fly aNewFly;
			if (movieLoaded) {
				aNewFly = new Fly(totalFrames);
			} else {
				aNewFly = new Fly();
			}
			aNewFly.addFrameInfo(frameNumber, tempFlies.get(0)[0],
					tempFlies.get(0)[1]);
			flies.add(aNewFly);
			tempFlies.remove(0);
		}
	}

	/**
	 * Adds to the list of Fly objects with their information gathered from the
	 * single given image. Also, adds the given file to the list of files stored
	 * within this Analyzer. In order to retrieve information, use the flies
	 * List in this Analyzer, through getFlies().
	 * 
	 * @param file
	 *            the file containing the image which is being analyzed.
	 */
	public void flydentify(File file) {
		try {
			images[totalFrames] = file;
			totalFrames++;
			BufferedImage image = ImageIO.read(file);
			flydentify(image, totalFrames - 1);
		} catch (IOException e) {
			System.err.println("EVERYTHING IS HORRIBLE");
			e.printStackTrace();
		}
	}

	/**
	 * This is the method called when analyzing a movie. IT IS INCOMPLETE.
	 * <p>
	 * This is just to show the format of how flydentifyMovie should work.
	 * MovieLoaded needs to be set to true for flydentify to call the proper Fly
	 * constructor. TotalFrames should be set to the total number of frames
	 * within the movie. Afterwards, call flydentify on all frames to set up fly
	 * data.
	 */
	public void flydentifyMovie() {
		movieLoaded = true;
		totalFrames = 0;
		images = new File[totalFrames];
	}

	/**
	 * Gives the List of Fly objects, which store any information gained from
	 * analyzed images.
	 * 
	 * @return the List of Fly objects, which contain information for individual
	 *         flies in the analyzed image or movie.
	 */
	public List<Fly> getFlies() {
		return flies;
	}

	/**
	 * Gives a file containing an image from the array of images in this
	 * Analyzer.
	 * 
	 * @param index
	 * @return
	 */
	public File getImage(int index) {
		if (!(index < 0) && index < images.length) {
			return images[index];
		}
		return null;
	}

	/**
	 * Getter for the total number of frames or images which have been processed
	 * by this Analyzer.
	 * 
	 * @return the total number of frames or images which have been processed.
	 */
	public int getTotalFrames() {
		return totalFrames;
	}

	/**
	 * Checks if the rgb value given is dark enough to be identified as a fly.
	 * 
	 * @param rgb
	 *            the integer rgb value to be judged if dark enough.
	 * @return true if the rgb value is dark enough, false if the rgb value is
	 *         too light.
	 */
	public boolean isDarkEnough(int rgb) {
		int red;
		int green;
		int blue;
		double avg;
		red = (rgb >> 16) & 0xFF;
		green = (rgb >> 8) & 0xFF;
		blue = rgb & 0xFF;
		avg = red * 0.2989 + green * .587 + blue * .114;
		boolean found = ((int) (Math.round(avg)) <= CONTRAST_THRESHOLD);
		return found;
	}

	/**
	 * Updates the size threshold field. This is used to determine if an object
	 * identified within an image is large enough to be considered a fly. This
	 * will also analyze all stored images again.
	 * 
	 * @param input
	 *            the value which size threshold will be set to.
	 */
	public void sizeThresholdUpdate(int input) {
		sizeThreshold = input;
		if (totalFrames > 0) {
			updateImages();
		}
	}

	/**
	 * This runs flydentify on all stored images.
	 */
	public void updateImages() {
		try {
			for (int i = 0; i < totalFrames; i++) {
				// TODO this should probably create a new flies List, since it
				// is re running flydentify on all images.
				BufferedImage image = ImageIO.read(images[i]);
				flydentify(image, i);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
