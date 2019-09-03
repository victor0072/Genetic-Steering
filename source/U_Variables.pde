//Universal Variable Settings
ArrayList<Rat> rats = new ArrayList<Rat>();
ArrayList<Food> food = new ArrayList<Food>();
ArrayList<Poison> poison = new ArrayList<Poison>();

//Buttons
boolean debug = false;
boolean ui = false;
boolean display = true;


//RatStats
float mutation = 0.01;
float ratMin = 51;
float ratHealthStart = 1;
float ratDeathSpeed = 0.005; //Health starts at ratHealthStart and subtracts by this every update
float attRange = 5; //Attraction Range
float ratSize = 3;
float ratSpeed = 4;
float ratForce = 0.03;
float babyLimit = 3; // once health reaches this limit spawn a Clone and a "baby".
float shootDebt = 0.01;
float maxRats = 75;

float foodSize = ratSpeed;
float foodValue = 1;
float poisonValue = 0.8;
float maxFood = 50;

//DNA MINMAX
float[] min = {-attRange, -attRange, -attRange, 10, 10, 10, 1, -1};
float[] max = {attRange, attRange, attRange, 100, 100, 100, 3, 1};
