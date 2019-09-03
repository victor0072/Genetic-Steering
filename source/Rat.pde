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
    health = ratHealthStart + random(-0.1, 0.1);
  }

  void update() {
    physics();
    boundries();
    behaviors(food, poison);
    shoot();
    updateHealth();
    timer++;
  }

  void updateHealth() {
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

  void createClone() {
    Rat baby = new Rat();
    baby.dna = dna;
    baby.mutate();
    baby.position = position.copy();
    health -=1;
    rats.add(baby);
  }

  void mutate() {
    for (int i = 0; i < dna.length; i++) {
      if (random(1) < mutation) {
        dna[i] = random(min[i], max[i]);
      }
      dna[i] += random(-0.001, 0.001);
      if (dna[i] > max[i]) {
        dna[i] = max[i];
      }
      if (dna[i] < min[i]) {
        dna[i] = min[i];
      }
    }
  }

  void genBaby() {
    Rat baby = new Rat();
    int Father = int(floor(random(rats.size())));
    int Mother = int(floor(random(rats.size())));

    for (int i = 0; i < dna.length; i ++) {
      int c = int(floor(random(2)));
      if (c == 0) {
        baby.dna[i] = rats.get(Mother).dna[i];
      } else if (c == 1) {
        baby.dna[i] = rats.get(Father).dna[i];
      }
    }
    baby.mutate();
    rats.add(baby);
  }



  void behaviors(ArrayList<Food> food, ArrayList<Poison> poison) {
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

  PVector eatFood(ArrayList<Food> list) {
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

  PVector eatPoison(ArrayList<Poison> list) {
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
          curforce *= 0.5;
          poison.remove(closest);
        } else if (position.dist(list.get(closest).position) < dna[4]) {
          return seek(list.get(closest).position);
        }
      }
    }



    return new PVector(0, 0);
  }

  PVector findEnemy(ArrayList<Rat> list) {
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
  void shoot() {
    shotDelay-= 0.01;
    if (shotDelay <= 0) {
      PVector dir = velocity.copy();
      dir.setMag(dna[7]);
      poison.add(new Poison(this, dir));
      health -= shootDebt;
      shotDelay = dna[6];
    }
  }

  void boundries() {
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

  void physics() {
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

  void applyForce(PVector force) {
    // We could add mass here if we want A = F / M
    acceleration.add(force);
  }

  // A method that calculates a steering force towards a target
  // STEER = DESIRED MINUS VELOCITY
  PVector seek(PVector target) {

    PVector desired = PVector.sub(target, position); // A vector pointing from the location to the target

    // Scale to maximum speed
    desired.setMag(maxspeed);

    // Steering = Desired minus velocity
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(curforce); // Limit to maximum steering force

    return steer;
    // applyForce(steer);
  }

  void draw() {
    // Draw a triangle rotated in the direction of velocity
    if (display) {
      float angle = velocity.heading() + PI / 2;

      push();
      color gr = color(0, 150, 0);
      color rd = color(150, 0, 0);
      color col = lerpColor(rd, gr, health);
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
