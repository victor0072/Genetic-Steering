import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.math.BigDecimal; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Evolutionary_Steering extends PApplet {



public void setup() {
  
  frameRate(99999);
  for (int i = 0; i < 50; i ++) {
    rats.add(new Rat());
  }
  for (int i = 0; i < 10; i ++) {
    food.add(new Food());
  }
  //for (int i = 0; i < 80; i ++) {
  //  poison.add(new Poison());
  //}
}

public void draw() {
  background(51);
  for (int i = 0; i < poison.size(); i++) {
    poison.get(i).update();
  }
  for (int i = 0; i < food.size(); i++) {
    food.get(i).update();
  }
  
  

  float[] advDNA = new float[rats.get(0).dna.length];
  for (int i = 0; i < rats.size(); i++) {
    for (int d = 0; d < rats.get(0).dna.length; d++) {
      advDNA[d] += rats.get(i).dna[d];
    }
    rats.get(i).update();
  }
  
  
  

  for (int i = 0; i < rats.get(0).dna.length; i++) {
    advDNA[i] /= rats.size();
  }
  
  
  
  
  
  
  if (display) {
    for (int i = 0; i < rats.size(); i++) {
      rats.get(i).draw();
    }
    for (int i = 0; i < food.size(); i++) {
      food.get(i).draw();
    }
    for (int i = 0; i < poison.size(); i++) {
      poison.get(i).draw();
    }
    
    if(ui){
      
    }
  }
}

public static float round(float d, int decimalPlace) {
  BigDecimal bd = new BigDecimal(Float.toString(d));
  bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
  return bd.floatValue();
}

public void keyPressed() {
  if (keyCode == 32) {
    debug = !debug;
  }
  if (keyCode == 17) {
    ui = !ui;
  }
  if (keyCode == 16) {
    display = !display;
  }
}
class Food {
  PVector position;
  PVector velocity = new PVector(0, 0);
  float fValue = foodValue;

  Food() {
    position = new PVector(random(width), random(height));
  }

  Food(Rat parent) {
    position = new PVector(parent.position.x, parent.position.y);
    this.velocity = parent.velocity;
  }

  public void update() {
    position.add(velocity);
    boundries();
  }

  public void draw() {
    if (display) {
      stroke(0, 150, 0);
      fill(0, 100, 0);
      ellipseMode(CENTER);
      ellipse(position.x, position.y, foodSize, foodSize);
    }
  }

  public void boundries() {
    if (position.x > width) {
      position.x = 0;
    }
    if (position.x < 0) {
      position.x = width;
    }
    if (position.y > height) {
      position.y = 0;
    }
    if (position.y < 0) {
      position.y = height;
    }
  }
}
class Poison {
  PVector position;
  PVector velocity = new PVector(0, 0);
  Rat parent = null;
  float health = 5;
  float pValue = poisonValue;

  Poison() {
    position = new PVector(random(width), random(height));
  }

  Poison(Rat parent, PVector velocity) {
    position = new PVector(parent.position.x, parent.position.y);
    this.parent = parent;
    this.velocity = velocity;
  }

  public void update() {
    position.add(velocity);
    boundries();
    health -= 0.01f;

    if (health <= 0) {
      poison.remove(this);
    }
  }

  public void draw() {
    if (display) {
      stroke(150, 0, 0);
      fill(100, 0, 0);
      ellipseMode(CENTER);
      ellipse(position.x, position.y, foodSize, foodSize);
    }
  }

  public void boundries() {
    if (position.x > width) {
      position.x = 0;
    }
    if (position.x < 0) {
      position.x = width;
    }
    if (position.y > height) {
      position.y = 0;
    }
    if (position.y < 0) {
      position.y = height;
    }
  }
}
class Rat {
  PVector acceleration;
  PVector velocity;
  PVector position;
  float r;
  float maxspeed;
  float curforce;
  float health;
  
  float timer = 0;

  //Food Attaction, Poison attraction, Enemy Attraction, Food range, Poison range, Enemy Range, Shot Delay, ShotSpeed 

  float[] dna = {random(min[0], max[0]), random(min[1], max[1]), random(min[2], max[2]), random(min[3], max[3]), random(min[4], max[4]), random(min[5], max[5]), random(min[6], max[6]), random(min[7], max[7])};

  Rat() {
    acceleration = new PVector(0, 0);
    velocity = new PVector(random(-2, 2), random(-2, 2));
    position = new PVector(random(width), random(height));
    r = ratSize;
    maxspeed = ratSpeed;
    curforce = ratForce;
    health = ratHealthStart + random(-0.1f, 0.1f);
  }

  public void update() {
    physics();
    boundries();
    behaviors(food, poison);
    shoot();
    updateHealth();
    timer++;
  }

  public void updateHealth() {
    health -= ratDeathSpeed;
    if (health > babyLimit) {
      createClone();
    }

    if (health <= 0) {
      if (food.size() < maxFood) {
        food.add(new Food(this));
      }
      if (rats.size() < ratMin) {
        genBaby();
      }
      rats.remove(this);
    }
  }

  public void createClone() {
    Rat baby = new Rat();
    baby.dna = dna;
    baby.mutate();
    baby.position = position.copy();
    health -=1;
    rats.add(baby);
  }

  public void mutate() {
    for (int i = 0; i < dna.length; i++) {
      if (random(1) < mutation) {
        dna[i] = random(min[i], max[i]);
      }
      dna[i] += random(-0.001f, 0.001f);
      if (dna[i] > max[i]) {
        dna[i] = max[i];
      }
      if (dna[i] < min[i]) {
        dna[i] = min[i];
      }
    }
  }

  public void genBaby() {
    Rat baby = new Rat();
    int Father = PApplet.parseInt(floor(random(rats.size())));
    int Mother = PApplet.parseInt(floor(random(rats.size())));

    for (int i = 0; i < dna.length; i ++) {
      int c = PApplet.parseInt(floor(random(2)));
      if (c == 0) {
        baby.dna[i] = rats.get(Mother).dna[i];
      } else if (c == 1) {
        baby.dna[i] = rats.get(Father).dna[i];
      }
    }
    baby.mutate();
    rats.add(baby);
  }



  public void behaviors(ArrayList<Food> food, ArrayList<Poison> poison) {
    PVector steerFood = eatFood(food);
    PVector steerPoison = eatPoison(poison);
    PVector steerEnemy = findEnemy(rats);

    steerFood.mult(dna[0]);
    steerPoison.mult(dna[1]);
    steerEnemy.mult(dna[2]);

    applyForce(steerFood);
    applyForce(steerPoison);
    applyForce(steerEnemy);
  }

  public PVector eatFood(ArrayList<Food> list) {
    float record =   99999999;
    int closest = -1;
    if (list.size() > 0) {
      for (int i = 0; i < list.size(); i++) {
        float d = position.dist(list.get(i).position);
        if (d < record) {
          record = d;
          closest = i;
        }
      }
    }
    if (closest > -1) {
      if (list.size() > 0) {
        if (position.dist(list.get(closest).position) < foodSize) {
          health += food.get(closest).fValue;
          food.remove(closest);
        } else if (position.dist(list.get(closest).position) < dna[3]) {
          return seek(list.get(closest).position);
        }
      }
    }

    return new PVector(0, 0);
  }

  public PVector eatPoison(ArrayList<Poison> list) {
    float record =   99999999;
    int closest = -1;
    if (list.size() > 0) {
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i).parent != this) {
          float d = position.dist(list.get(i).position);
          if (d < record) {
            record = d;
            closest = i;
          }
        }
      }
    }
    if (closest > -1) {
      if (list.size() > 0) {
        if (position.dist(list.get(closest).position) < foodSize) {
          health -= poison.get(closest).pValue;
          curforce *= 0.5f;
          poison.remove(closest);
        } else if (position.dist(list.get(closest).position) < dna[4]) {
          return seek(list.get(closest).position);
        }
      }
    }



    return new PVector(0, 0);
  }

  public PVector findEnemy(ArrayList<Rat> list) {
    float record =   99999999;
    int closest = -1;
    if (list.size() > 0) {
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i) != this) {
          float d = position.dist(list.get(i).position);
          if (d < record) {
            record = d;
            closest = i;
          }
        }
      }
    }
    if (closest > -1) {
      if (list.size() > 0) {
        if (position.dist(list.get(closest).position) < dna[2]) {
          return seek(list.get(closest).position);
        }
      }
    }
    return new PVector(0, 0);
  }

  float shotDelay = dna[6];
  public void shoot() {
    shotDelay-= 0.01f;
    if (shotDelay <= 0) {
      PVector dir = velocity.copy();
      dir.setMag(dna[7]);
      poison.add(new Poison(this, dir));
      health -= shootDebt;
      shotDelay = dna[6];
    }
  }

  public void boundries() {
    if (position.x > width) {
      position.x = 0;
    }
    if (position.x < 0) {
      position.x = width;
    }
    if (position.y > height) {
      position.y = 0;
    }
    if (position.y < 0) {
      position.y = height;
    }
  }

  public void physics() {
    //handles after poison slowdown regeneration
    if (curforce < ratForce) {
      curforce+= ratForce/100;
    } else if (curforce > ratForce) {
      curforce = ratForce;
    }
    // Update velocity
    velocity.add(acceleration);
    // Limit speed
    velocity.limit(maxspeed);
    position.add(velocity);
    // Reset acceleration to 0 each cycle
    acceleration.mult(0);
  }

  public void applyForce(PVector force) {
    // We could add mass here if we want A = F / M
    acceleration.add(force);
  }

  // A method that calculates a steering force towards a target
  // STEER = DESIRED MINUS VELOCITY
  public PVector seek(PVector target) {

    PVector desired = PVector.sub(target, position); // A vector pointing from the location to the target

    // Scale to maximum speed
    desired.setMag(maxspeed);

    // Steering = Desired minus velocity
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(curforce); // Limit to maximum steering force

    return steer;
    // applyForce(steer);
  }

  public void draw() {
    // Draw a triangle rotated in the direction of velocity
    if (display) {
      float angle = velocity.heading() + PI / 2;

      push();
      int gr = color(0, 150, 0);
      int rd = color(150, 0, 0);
      int col = lerpColor(rd, gr, health);
      stroke(col);
      strokeWeight(1);
      fill(col, 150);
      translate(position.x, position.y);
      rotate(angle);
      beginShape();
      vertex(0, -r * 2);
      vertex(-r, r * 2);
      vertex(r, r * 2);
      endShape(CLOSE);

      if (debug) {
        ellipseMode(CENTER);
        stroke(gr, 150);
        strokeWeight(1);
        noFill();
        line(0, 0, 0, ((dna[0]+5)/2)*r);
        ellipse(0, 0, dna[3], dna[3]);
        stroke(rd, 150);
        line(-2, 0, 0, ((dna[1]+5)/2)*r);
        ellipse(0, 0, dna[4], dna[4]);
        stroke(0, 0, 255, 150);
        line(2, 0, 0, ((dna[2]+5)/2)*r);
        ellipse(0, 0, dna[5], dna[5]);
      }
      pop();
    }
  }
}
//Universal Variable Settings
ArrayList<Rat> rats = new ArrayList<Rat>();
ArrayList<Food> food = new ArrayList<Food>();
ArrayList<Poison> poison = new ArrayList<Poison>();

//Buttons
boolean debug = false;
boolean ui = false;
boolean display = true;


//RatStats
float mutation = 0.01f;
float ratMin = 51;
float ratHealthStart = 1;
float ratDeathSpeed = 0.005f; //Health starts at ratHealthStart and subtracts by this every update
float attRange = 5; //Attraction Range
float ratSize = 3;
float ratSpeed = 4;
float ratForce = 0.03f;
float babyLimit = 3; // once health reaches this limit spawn a Clone and a "baby".
float shootDebt = 0.01f;
float maxRats = 75;

float foodSize = ratSpeed;
float foodValue = 1;
float poisonValue = 0.8f;
float maxFood = 50;

//DNA MINMAX
float[] min = {-attRange, -attRange, -attRange, 10, 10, 10, 1, -1};
float[] max = {attRange, attRange, attRange, 100, 100, 100, 3, 1};
  public void settings() {  size(1200, 700); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Evolutionary_Steering" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
