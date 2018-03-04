import os


def do_svgo(path):
    list = os.walk(path)
    for root, dir, files in list:
        for file in files:
            if file[-3:] == "svg":
                os.system('svgo ./%s' % (file))


do_svgo("/home/hoshino/Projects/IntelliJ/Kotlin/covscript-intellij/res/icons/svgs")
