package org.covscript.lang

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun task1() {
	val csc = ImageIO.read(File("res/icons/csc.png"))
	val csp = ImageIO.read(File("res/icons/csp.png"))
	val cse = ImageIO.read(File("res/icons/cse.png"))
	(0 until csc.width).forEach { x ->
		(0 until csc.height).forEach { y ->
			dealWithPixel(csc, x, y)
			dealWithPixel(csp, x, y)
			dealWithPixel(cse, x, y)
		}
	}
	ImageIO.write(csc, "PNG", File("csc.png"))
	ImageIO.write(cse, "PNG", File("cse.png"))
	ImageIO.write(csp, "PNG", File("csp.png"))
}

fun task2() {
	val csp = ImageIO.read(File("res/icons/csp.png"))
	(0 until csp.width).forEach { x ->
		(0 until csp.height).forEach { y ->
			val o = csp.getRGB(x, y)
			print((Color(o).rgb + 0xFFFFFF).toString(16))
			print(' ')
			if (Color(o).rgb + 0xFFFFFF == 0x2B2B2A) csp.setRGB(x, y, 0x9FEFEFEF.toInt())
		}
		println()
	}
	ImageIO.write(csp, "PNG", File("csp.png"))
}

fun task3() {
	val csp = ImageIO.read(File("res/icons/cov.png"))
	(0 until csp.width).forEach { x ->
		(0 until csp.height).forEach { y ->
			val o = csp.getRGB(x, y)
			print((Color(o).rgb + 0xFFFFFF).toString(16))
			print(' ')
			when (Color(o).rgb + 0xFFFFFF) {
				0xFFFFFE -> csp.setRGB(x, y, 0)
				0x99D9E9 -> csp.setRGB(x, y, 0x8E99D9E9.toInt())
			}
		}
		println()
	}
	ImageIO.write(csp, "PNG", File("cov.png"))
}

fun Array<String>.main() {
	task3()
}

val com = Color(127, 127, 127)

private fun dealWithPixel(csc: BufferedImage, x: Int, y: Int) {
	val o = csc.getRGB(x, y)
	if (Color(o) == Color.WHITE) csc.setRGB(x, y, 0x00FFFFFF and o)
	else if (Color(o) == com) csc.setRGB(x, y, 0x7F2B2B2B)
}
