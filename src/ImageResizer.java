import java.nio.file.Paths;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;
import java.io.IOException;

public class ImageResizer {
	// Status refers to whether or not there was a single error or warning in the
	// program. If there is a single error, status document will be named "Failure".
	// If not, it is a "Success"
	static Boolean status = true;
	static String path = Paths.get("").toAbsolutePath().toString();

	public static void main(String[] args) throws IOException {

		// Generates a list of files in /photos folder
		File folder = new File(path + "/photos");
		String[] files = folder.list();

		// Deletes previous text files:
		deleteFile("status.txt");
		deleteFile("Success.txt");
		deleteFile("Failure.txt");

		// Creates a new status file:
		File fileStatus = new File("status.txt");
		fileStatus.createNewFile();

		for (String file : files) {
			if (!file.equals(".DS_Store")) { // ignores .DS_Store files which will crash the program
				BufferedImage img = ImageIO.read(new File(path + "/photos/" + file));

				// Gets the width and height of the original image
				int imgWidth = img.getWidth(null);
				int imgHeight = img.getHeight(null);

				status = imageShrinker(file, img, imgWidth, imgHeight);
			}

		}

		// Changes the status of the file to "Success" or "Failure"
		File tempStatus;
		if (status == true) {
			tempStatus = new File("Success.txt");
		} else {
			tempStatus = new File("Failure.txt");
		}
		fileStatus.renameTo(tempStatus);
	}

	/*
	 * Resizes the image if it has dimensions greater than 300x300. Takes in the
	 * file name, image and width and height, shrinks the image and returns true if
	 * there have been 0 warnings and false if there has been a warning in this call
	 * of the method or a previous one
	 */

	private static boolean imageShrinker(String file, BufferedImage img, int imgWidth, int imgHeight)
			throws IOException {
		File fileStatus = new File("status.txt");
		FileWriter writer = new FileWriter(fileStatus, true);
		writer.write(file + " ");
		writer.write(imgWidth + "x" + imgHeight);

		// If the image has a dimension less than 300, then it is too small and we will
		// not shrink it.
		if (imgWidth < 300 || imgHeight < 300) {
			writer.write(" WARNING: Photo is too small! May not appear on website if uploaded\n");
			writer.close();
			return false;
			// If the image has a dimension that is 300, then it is already the right size
			// or has already been resized
		} else if (imgWidth == 300 || imgHeight == 300) {
			if (imgHeight >= 650 || imgWidth >= 650) { // except if it has a dimension that is greater than 650
				writer.write(" WARNING: Photo may be too large and may slow down webpage.");
			} else {
				writer.write(" Image has already been resized.");
			}
			writer.write("\n");
			writer.close();
			return false;
			/*
			 * If not, then the image is ready to be resized by making the smaller of the
			 * width or height equal to 300, then using the newly created ratio to change
			 * the larger dimension.
			 */
		} else {
			if (imgWidth < imgHeight) {
				int newWidth = 300;
				int newHeight = (int) (((double) newWidth / imgWidth) * imgHeight); // resizes the height based on the
																					// width ratio
				BufferedImage resized = resize(img, newHeight, newWidth);
				File output = new File(path + "/photos/New" + file);
				output.createNewFile();
				ImageIO.write(resized, "png", output);
				
				deleteFile(path + "/photos/" + file);
				File temp = new File(path + "/photos/" + file);
				output.renameTo(temp);
				writer.write(" changed to " + newWidth + "x" + newHeight);
				if (newHeight >= 650 || newWidth >= 650) {
					writer.write(" WARNING: Photo may be too large and may slow down webpage.");
				}
				writer.write("\n");
				writer.close();
				if (status == false)
					return false;
				return true;
			} else {
				int newHeight = 300;
				int newWidth = (int) (((double) newHeight / imgHeight) * imgWidth); // resizes the width based on the
																					// height ratio
				BufferedImage resized = resize(img, newHeight, newWidth);
				File output = new File(path + "/photos/New" + file);
				output.createNewFile();
				ImageIO.write(resized, "png", output);

				// Delete original large photo and replace the name with the new smaller photo
				deleteFile(path + "/photos/" + file);
				File temp = new File(path + "/photos/" + file);
				output.renameTo(temp);

				writer.write(" changed to " + newWidth + "x" + newHeight);
				if (newHeight >= 650 || newWidth >= 650) { // Image is either too long or too wide for a webpage
					writer.write(" WARNING: Photo may be too large and may slow down webpage.");
				}
				writer.write("\n");
				writer.close();

				// If status has already had a warning, then it will remain false.
				if (status == false)
					return false;
				return true;
			}
		}
	}

	// Resizes image given the image, new height and new width
	private static BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	// 
	private static void deleteFile(String filename) {
		File deleteFile = new File(filename);
		deleteFile.delete();
	}

}
