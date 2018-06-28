/////my
import lejos.subsumption.*;
import lejos.nxt.*;
import lejos.navigation.*;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import java.io.*;
import javax.bluetooth.*;
import lejos.nxt.comm.NXTConnection;

///////////////////////////////////////////////////////////////////////
class BehaviorTowardsPeople implements Behavior
{
BTConnection btc;
UltrasonicSensor us1;
Pilot robot;
boolean girar=false;
double x1;
double y1;
double x2;
double y2;
double x;
double y;
int Aux1;
int Aux2;
int grados=0;


public BehaviorTowardsPeople(UltrasonicSensor us1, Pilot p, BTConnection btc)
{
this.us1=us1;
this.robot=p;
this.btc=btc;
}

public boolean takeControl()
{
btc.setIOMode(NXTConnection.RAW);
DataInputStream dis = btc.openDataInputStream();

				try {
                    int n = dis.readInt();
                    int n1 = dis.readInt();
                    int n2 = dis.readInt();
                    Aux1=n;
                    Aux2=240-Aux1;
                    x1=(double)(Aux2);
                    y1=20;

                    if(((us1.getDistance()>20))){
                        girar=true;
                        x2=0;
                        y2=1;
                        }

                    else{
                        girar=true;
                        x2=1000;
                        y2=10;
                        }

                    x=x1+x2;
                    y=y1+y2;
                    grados=(int)((Math.atan(x/y))*(180/Math.PI));
                    }
                catch (IOException e) {
                    System.exit(0);
                    }
return girar;
}

public void action ()
{
robot.stop();
robot.rotate(grados);
robot.forward();
}

public void suppress()
{
robot.stop();
}
}

class BehaviorReachGoal implements Behavior
{
UltrasonicSensor us1;
Pilot robot;
BTConnection btc;
int parar;
int grados=0;
boolean girar=false;

public BehaviorReachGoal(UltrasonicSensor us1, Pilot p, BTConnection btc)
{
this.us1=us1;
this.robot=p;
this.btc=btc;
}

public boolean takeControl()
{
btc.setIOMode(NXTConnection.RAW);
DataInputStream dis = btc.openDataInputStream();

				try {
                    int n = dis.readInt();
                    int n1 = dis.readInt();
                    int n2 = dis.readInt();
                    parar=n2;

                    if(parar>160000 && us1.getDistance()<35){
                        girar=true;
                        grados=180;
                        }

                    else{
                        girar=false;
                        }
                    }
                catch (IOException e) {
                    System.exit(0);
                    }
return girar;
}

public void action ()
{
robot.stop();
try {
    Thread.sleep(5000);                 //1000 milliseconds is one second.
    }
catch(InterruptedException ex) {
    Thread.currentThread().interrupt();
    }
robot.rotate(grados);
robot.forward();
}

public void suppress()
{
robot.stop();
}
}
////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////
class BehaviorEscape implements Behavior
{

boolean escape=false;

public BehaviorEscape ()
{
}

public boolean takeControl()
{
if(Button.ESCAPE.isPressed()){
    escape = true;
    }
return escape;
}

public void action ()
{
    System.exit(0);

}

public void suppress()
{
}
}
//////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
public class Myversion2
{

static final float WHEEL_DIAM=5.6f;
static final float TRACK_W=11.5f;
static BTConnection btc = Bluetooth.waitForConnection();

public static void main (String [] args)
{
UltrasonicSensor us1 = new UltrasonicSensor(SensorPort.S4);
Pilot robot = new Pilot(WHEEL_DIAM, TRACK_W, Motor.A, Motor.C);
robot.setSpeed(100);
Behavior b2= new BehaviorTowardsPeople(us1, robot, btc);
Behavior b3= new BehaviorReachGoal(us1, robot, btc);
Behavior b5 = new BehaviorEscape();
Behavior [] bArray={b2,b3,b5};
Arbitrator arby = new Arbitrator(bArray, false);
arby.start();
}
}
