import googlecode_upload
import sys, os

if len(sys.argv) != 2:
   raise Exception("missing version number.")
version = sys.argv[1]   
underversion = version.replace('.','_')  
print "Uploading version %s" % version
home = os.getenv('GENYRIS_HOME')
password = os.getenv('GOOGLE_PASSWORD')

def gupload(file, summary, labels):
   statuscode, msg, url = googlecode_upload.upload("%s/rel/%s" % (home,file), 
      'Genyris', 
      'birchb1024@gmail.com',
      password,
      summary % version, labels)
   print "upload: %s %s %s" % (statuscode, msg, url)
   if( statuscode != 201):
	 print "*** Googlecode upload failed: %s %s %s" % (statuscode, msg, url)
	 print "continuing..."
gupload("genyris-binary-%s.zip.MD5" % version, "Genyris %s binary checksum", ['Type-Docs'])
gupload("genyris-binary-%s.zip" % version, "Genyris %s binary", ['OpSys-All'])
gupload("genyris_linux_%s.deb" % underversion, "Genyris %s Debian package", ['Type-Installer','OpSys-Linux'])
gupload("genyris_macos_%s.dmg" % underversion, "Genyris %s Mac OS/X package", ['Type-Installer','OpSys-OSX'])
gupload("genyris-manual-%s.html" % version, "Genyris %s Manual (HTML)", ['Type-Docs'])
gupload("genyris-manual-%s.pdf" % version, "Genyris %s Manual (PDF)", ['Type-Docs'])
gupload("genyris_windows_%s.exe" % underversion, "Genyris %s Windows Installer", ['Type-Installer','OpSys-Windows'])



   

