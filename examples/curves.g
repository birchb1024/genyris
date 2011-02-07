@prefix java 'http://www.genyris.org/lang/java#'
@prefix u "http://www.genyris.org/lang/utilities#"

include 'examples/swing-util.g'

java:import double
java:import 'java.lang.Math' as Math

Bignum
    def .sqrt()
        Math!sqrt-double .self
    def .cos()
        Math!cos-double .self
    def .sin()
        Math!sin-double .self

var minlength 4
var curx 50
var cury 50
def draw (x1 y1)
     .drawLine-int-int-int-int curx cury x1 y1
     setq curx x1
     setq cury y1

def plotline (length angle)
   draw
      + curx (* 2 (angle(.cos)) length)
      + cury (* (angle(.sin)) length)

var pi/4 (/ Math!PI 4.0)
var sqrt2 (2.0(.sqrt))
def ccurve-aux (length angle)
   cond 
         (< length minlength)
            plotline length angle
         else 
            ccurve-aux 
               / length sqrt2
               + angle pi/4
            ccurve-aux 
                / length sqrt2
                - angle pi/4

def c-curve(width height)
    .setColor-java_awt_Color Color!green
    setq curx 100
    setq cury 50
    ccurve-aux 80 0             
    
def dragon-aux (length angle sign)
  cond 
     (< length minlength)
          plotline length angle
     else  
        dragon-aux 
            / length sqrt2
            + angle (* sign pi/4) 
            ~ 1.0
        dragon-aux 
            / length sqrt2
            - angle (* sign pi/4) 
            ~ -1.0 

def dragon-curve(width height)
    .setColor-java_awt_Color Color!blue
    setq curx 300
    setq cury 100
    dragon-aux 240 45 1             

plot-window 'C Curve' c-curve 400 200
plot-window 'Dragon Curve' dragon-curve 700 400

    