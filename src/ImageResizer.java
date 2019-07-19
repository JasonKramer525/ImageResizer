import java.nio.file.Paths;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;
import java.io.IOException;

public class ImageResizer {
	static Boolean status = true;

	public static void main(String[] args) throws IOException {

		File folder = new File(Paths.get("").toAbsolutePath().toString() + "/photos");
		System.out.println(Paths.get("").toAbsolutePath().toString() + "/photos");
		String[] files = folder.list();

		//Deletes previous text files:
		File oldStatus = new File("status.txt");
		oldStatus.delete();
		File oldStatus2 = new File("Success.txt");
		oldStatus2.delete();
		File oldStatus3 = new File("Failure.txt");
		oldStatus3.delete();

		//Creates a new status file:
		File fileStatus = new File("status.txt");
		fileStatus.createNewFile();

		for (String file : files) {
			System.out.println(file);
			if (!file.equals(".DS_Store")) {
				BufferedImage img = ImageIO
						.read(new File(Paths.get("").toAbsolutePath().toString() + "/photos/" + file));

				int imgWidth = img.getWidth(null);
				System.out.println(imgWidth);
				int imgHeight = img.getHeight(null);
				System.out.println(imgHeight);

				status = imageShrinker(file, img, imgWidth, imgHeight);
			}

		}
		System.out.println(status);
		File tempStatus;
		if (status == true) {
			tempStatus = new File("Success.txt");
		} else {
			tempStatus = new File("Failure.txt");
		}
		fileStatus.renameTo(tempStatus);

	}

	private static boolean imageShrinker(String file, BufferedImage img, int imgWidth, int imgHeight)
			throws IOException {
		File fileStatus = new File("status.txt");
		FileWriter writer = new FileWriter(fileStatus, true);
		writer.write(file + " ");
		writer.write(imgWidth + "x" + imgHeight);

		if (imgWidth < 300 || imgHeight < 300) {
			writer.write(" WARNING: Photo is too small! May not appear on website if uploaded\n");
			writer.close();
			return false;
		} else if (imgWidth == 300 || imgHeight == 300) {
			if (imgHeight >= 650 || imgWidth >= 650) {
				writer.write(" WARNING: Photo may be too large and may slow down webpage.");
			}
			else {
			writer.write(" Image has already been resized.");
			}
			writer.write("\n");
			writer.close();
			return false;
		} else {
			if (imgWidth < imgHeight) {
				int newWidth = 300;
				int newHeight = (int) (((double) newWidth / imgWidth) * imgHeight);
				System.out.println(newWidth + "x" + newHeight);
				BufferedImage resized = resize(img, newHeight, newWidth);
				File output = new File(Paths.get("").toAbsolutePath().toString() + "/photos/New" + file);
				output.createNewFile();
				ImageIO.write(resized, "png", output);
				File filo = new File(Paths.get("").toAbsolutePath().toString() + "/photos/" + file);
				filo.delete();
				File temp = new File(Paths.get("").toAbsolutePath().toString() + "/photos/" + file);
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
				int newWidth = (int) (((double) newHeight / imgHeight) * imgWidth);
				System.out.println("Test 1: " + imgHeight);
				System.out.println(newWidth + "x" + newHeight);
				BufferedImage resized = resize(img, newHeight, newWidth);
				File output = new File(Paths.get("").toAbsolutePath().toString() + "/photos/New" + file);
				output.createNewFile();
				ImageIO.write(resized, "png", output);
				File filo = new File(Paths.get("").toAbsolutePath().toString() + "/photos/" + file);
				filo.delete();
				File temp = new File(Paths.get("").toAbsolutePath().toString() + "/photos/" + file);
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
			}
		}
	}

	private static BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

}
