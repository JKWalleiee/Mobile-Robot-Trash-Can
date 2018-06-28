# Mobile-Robot-Trash-Can

The objective in this project was to assist people in the handling of garbage inside an office, by allowing a robot to receive the garbage of people and take it to the collection point more near. In this project, artificial vision techniques were used to detect groups of people through a camera, and autonomous navigation techniques for planning trajectories towards people. In this project a garbage collection point was not set up. The robot used was a lego NXT.

### Myversion2.java
behaviors based control.

• BehaviorTowardsPeople.

• BehaviorReachGoal.

• BehaviorEscape.


### MainActivity.java
Acquisition and processing of images.

A background subtraction was used. The OpenCV version was not used, instead the algorithm was programmed from scratch.


