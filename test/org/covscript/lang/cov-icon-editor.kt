package org.covscript.lang

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


fun Array<String>.main() {
	val csc = ImageIO.read(File("res/icons/csc.png"))
	val csp = ImageIO.read(File("res/icons/csp.png"))
	val cse = ImageIO.read(File("res/icons/cse.png"))
	(0 until csc.width).forEach { x ->
		(0 until csc.height).forEach { y ->
			dealWithPixel(csc, x, y)
			dealWithPixel(csp, x, y)
			dealWithPixel(cse, x, y)
		}
		println()
	}
	ImageIO.write(csc, "PNG", File("csc.png"))
	ImageIO.write(cse, "PNG", File("cse.png"))
	ImageIO.write(csp, "PNG", File("csp.png"))
}

val com = Color(127, 127, 127)

private fun dealWithPixel(csc: BufferedImage, x: Int, y: Int) {
	val o = csc.getRGB(x, y)
	if (Color(o) == Color.WHITE) csc.setRGB(x, y, 0x00FFFFFF and o)
	else if (Color(o) == com) csc.setRGB(x, y, 0x7F2B2B2B)
}
