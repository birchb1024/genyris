import googlecode_upload
import sys, os

if len(sys.argv) != 2:
   raise "missing version number."
version = sys.argv[1]   
winversion = version.replace('.','_')  
print "Uploading version %s" % version
home = os.getenv('GENYRIS_HOME')
password = os.getenv('GOOGLE_PASSWORD')

def gupload(file, summary, labels):
   print googlecode_upload.upload("%s/rel/%s" % (home,file), 
      'Genyris', 
      'birchb1024@gmail.com',
      password,
      summary % version, labels)
   
gupload("genyris-binary-%s.zip.MD5" % version, "Genyris %s binary checksum", ['Type-Docs'])
gupload("genyris-binary-%s.zip" % version, "Genyris %s binary", ['OpSys-All'])
gupload("genyris_linux_%s.deb" % version, "Genyris %s Debian package", ['Type-Installer','OpSys-Linux'])
gupload("genyris-manual-%s.html" % version, "Genyris %s Manual (HTML)", ['Type-Docs'])
gupload("genyris-manual-%s.pdf" % version, "Genyris %s Manual (PDF)", ['Type-Docs'])
gupload("genyris_windows_%s.exe" % winversion, "Genyris %s Windows Installer", ['Type-Installer','OpSys-Windows'])



   

