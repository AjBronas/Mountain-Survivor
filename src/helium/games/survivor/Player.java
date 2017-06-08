package helium.games.survivor;

import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;

public class Player {

	public final int WIDTH = 50;
	public final int HEIGHT = 50;

	public String player_name = "Jack Survivor";
	public int health = 100;
	public int maxHealth = 100;
	public int hunger = 100;
	public int thirst = 100;
	public int survived = 0;	
	private int x = 0;
	private int y = 0;
	public int size = 50;
	public Bitmap image_idle;
	public Bitmap image_dead;
	public String facing = "Right";
	public boolean isOnGround = false;
	public int cameraX=0;
	public int screenwidth = 1;
	public int screenheight = 1;
	public int jumpheight = 50;
	public boolean canJump = true;
	public boolean canMoveRight = true;
	public boolean canMoveLeft = true;
	public Blocks lastTouchedBlock;
	public String movingDir = Level.NO;
	public boolean isCold = false;
	public Weapon weapon;
	public boolean canAttack = true;
	public Animation animation;
	public int coldDepre = 2;
	public int coldDepreInt = 0;
	public int hungerDepre = 3;
	public int hungerDepreInt = 0;
	public int thirstDepre = 2;
	public int thirstDepreInt = 0;
	public int healthDepre = 2;
	public int healthDepreInt = 0;
	public List<InventoryItem> inventory;
	public Resources res;
	public Level level;
	Bitmap image1;
	Bitmap image2;

	public Player(String playername, Resources res, Level level) {

		this.player_name = playername;
		this.image_idle = BitmapFactory.decodeResource(res, R.drawable.playeridle);
		this.animation = new Animation(image_idle, x, y, size, size);
		image1 = BitmapFactory.decodeResource(res, R.drawable.playerwalk1);
		image2 = BitmapFactory.decodeResource(res, R.drawable.playerwalk2);
		this.res = res;
		this.level = level;
	}

	public void spawn( int x, int y, int size ){
		this.x = x;
		this.y = y;
		this.setSize(size);
		this.weapon = new Weapon( res, level, Weapon.AXE );
		this.weapon.setX(this.getX());
		this.weapon.setY(this.getY());
	}

	public void setHealth(int health){
		this.health = health;
	}

	public void setHunger(int hunger){
		this.hunger = hunger;
	}

	public void setThirst(int thirst){
		this.thirst = thirst;
	}

	public void setSurvived(int days){
		survived = days;
	}

	public void setX(int x){

		this.x = x;
		this.weapon.setX(x + size);

	}

	public void setY(int y){

		this.y = y;
		this.weapon.setY(y);

	}

	public void updateStatus(){

		if( hungerDepreInt >= hungerDepre ){

			eat( -1 );
			hungerDepreInt = 0;
		}else{
			hungerDepreInt ++;
		}

		if( thirstDepreInt >= thirstDepre ){

			drink( -1 );
			thirstDepreInt = 0;
		}else{
			thirstDepreInt ++;
		}


		if( isCold == true ){
			if( !weapon.name.equals(Weapon.TORCH) ){
				if( coldDepreInt >= coldDepre ){

					heal( -1 );
					coldDepreInt = 0;
				}else{

					coldDepreInt ++;
				}

			}

		}

		if( hunger >= 100 ){
			if( isCold == false ){
				heal(1);

			}
		}

		if( thirst >= 100 ){
			if( isCold == false ){
				heal(1);

			}
		}

		if( hunger <= 0 ){
			heal( -1 );
		}

		if( thirst <= 0 ){
			heal( -1 );
		}

	}

	public void setSize( int size ){

		this.size = size;
		image_idle = Bitmap.createScaledBitmap(image_idle, size, size, true);
		image1 = Bitmap.createScaledBitmap(image1, size, size, true);
		image2 = Bitmap.createScaledBitmap(image2, size, size, true);

		Bitmap images[] = {image_idle,image1,image2,image1};
		int durations[] = {100,400,400,400};

		this.animation.createAnimation(images, durations, false, 0);
		this.animation.start(0, 0);
	}

	public void setDirection(String facing){

		this.facing = facing;

	}

	public void setWeapon( String type ){

		weapon.setWeapon(type);
		weapon.setX(this.getX());
		weapon.setY(this.getY());

	}

	public void heal( int health ){


		if(this.health <= 0){

			this.health = 0;
			//die();
		}else{
			if( healthDepreInt >= healthDepre ){

				this.health += health;
				healthDepreInt = 0;
			}else{

				healthDepreInt ++;
			}

		}

		if(this.health > this.maxHealth){

			this.health = this.maxHealth;
		}


	}

	public void eat( int foodAmount ){

		if( foodAmount < 0 ){
			if( this.hunger - foodAmount > 1 ){

				this.hunger += foodAmount;
			}else{

				this.hunger = 0;
			}
		}else{

			this.hunger += foodAmount;
		}

		if(this.hunger > this.maxHealth){

			this.hunger = this.maxHealth;
		}
	}

	public void drink( int amount ){

		if( amount < 0 ){
			if( this.thirst - amount > 1 ){

				this.thirst += amount;
			}else{

				this.thirst = 0;
			}
		}else{

			this.thirst += amount;
		}

		if(this.thirst > this.maxHealth){

			this.thirst = this.maxHealth;
		}
	}

	public void attack( List<ScreenObject> objslist, int camX){

		for ( ScreenObject objs: objslist){
			Rect Col = objs.Collision();
			int left = Col.left - camX;
			int right = Col.right - camX;
			int top = Col.top;
			int bottom = Col.bottom;
			Col = new Rect(left,top,right,bottom);
			if( Rect.intersects(Col, weapon.Collision())){

				objs.hit(weapon, 0);

			}
		}
		weapon.use();
		canAttack = false;

	}
	public int getX(){

		return this.x;

	}

	public int getY(){

		return this.y;

	}

	public String getDirection(){

		return this.facing;

	}


	public void moveX(int amount){

		this.x+=amount;
		if(weapon.tossed == false){
			this.weapon.x = x + size;
		}


	}

	public void moveY(int amount){

		this.y+=amount;
		if(weapon.tossed == false){
			this.weapon.y = y;
		}

	}

	public boolean isTouching( String type, String name ){
		
		boolean touching = false;
		
		if( level.blstring.contains( type ) || level.blstring.contains( name )){
			touching = true;
		}
		
		return touching;
	}

	public boolean isOffScreen(){

		boolean isoffscreen = false;
		if(this.getX()>=(0-this.size)&this.getY()>=(0-this.size)){

			if(this.getX()<=(Level.screen_width+this.size)&this.getY()>=(Level.screen_height+this.size)){

				isoffscreen = true;

			}

		}


		return isoffscreen;

	}

	public Rect bodyCollision(){

		Rect r = new Rect(this.x,this.y,this.x+this.size,this.y+this.size);
		return r;

	}

	public Rect feetCollision(){

		Rect r = new Rect(this.x+(this.size/3),this.y+(this.size/2),(this.x+this.size)-(this.size/3),this.y+this.size);
		return r;

	}
	public Rect rightCollision(){

		Rect r = new Rect(this.x + (this.size/2),this.y,this.x+this.size,this.y+(this.size/2)+(this.size/4));
		return r;

	}
	public Rect leftCollision(){

		Rect r = new Rect(this.x,this.y,this.x+(this.size/2),this.y+(this.size/2)+(this.size/4));
		return r;

	}

	public int getHealth(){
		return this.health;
	}

	public int getHunger(){
		return this.hunger;
	}

	public int getThirst(){
		return this.thirst;
	}

	public void jump(){
		if(canJump == true){
			if(isOnGround==true){
				this.setY(this.y-jumpheight);
				this.heal( -10 );
				this.isOnGround = false;
			}
		}
	}


}
