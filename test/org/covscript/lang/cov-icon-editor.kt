package org.covscript.lang

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


fun Array<String>.main() {
	val lice = ImageIO.read(File("lice.png"))
	val csc = ImageIO.read(File("csc.png"))
	val csp = ImageIO.read(File("csp.png"))
	val cse = ImageIO.read(File("cse.png"))
	val alphaPosition = 0xFF shl 24
	val bottomAlpha = lice.getRGB(lice.width - 1, lice.height - 1) and alphaPosition
	(0 until lice.width).forEach { x ->
		(0 until lice.height).forEach { y ->
			dealWithPixel(csc, x, y, lice, alphaPosition, bottomAlpha)
			dealWithPixel(csp, x, y, lice, alphaPosition, bottomAlpha)
			dealWithPixel(cse, x, y, lice, alphaPosition, bottomAlpha)
		}
	}
	ImageIO.write(csc, "PNG", File("csc-edited.png"))
	ImageIO.write(cse, "PNG", File("cse-edited.png"))
	ImageIO.write(csp, "PNG", File("csp-edited.png"))
}

private fun dealWithPixel(csc: BufferedImage, x: Int, y: Int, lice: BufferedImage, alphaPosition: Int, bottomAlpha: Int) {
	val o = csc.getRGB(x, y)
	csc.setRGB(x, y, if (y <= 9 || x < 1) o + (lice.getRGB(x, y) and alphaPosition) else o + bottomAlpha)
}