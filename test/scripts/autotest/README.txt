This directory implements a testing facility for GeoGebra and
GeoGebraWeb. It tries to compile and run the desktop version
of GeoGebra, and also compiles GeoGebraWeb (with GWT).

The bulldog script starts an endless loop by checking for the newest SVN
version in every 5 minute (build-latest). Then it calls build-desktop
and build-web for the two different builds. The alarm script sends
an email notification when an error occurs during the simpletest script.

Fine tuning is possible via autotest.conf, see autotest.conf.dist for
an example.

The simpletest script assumes that you have xvfb installed. This is
needed for checking the Java GUI automatically (without using a
real graphical system).

@author Zoltan Kovacs <zoltan@geogebra.org>
