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

  void update() {
    position.add(velocity);
    boundries();
    health -= 0.01;

    if (health <= 0) {
      poison.remove(this);
    }
  }

  void draw() {
    if (display) {
      stroke(150, 0, 0);
      fill(100, 0, 0);
      ellipseMode(CENTER);
      ellipse(position.x, position.y, foodSize, foodSize);
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
}
