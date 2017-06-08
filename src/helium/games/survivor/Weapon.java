package helium.games.survivor;

import java.util.List;

import javax.xml.datatype.Duration;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;

public class Weapon extends ScreenObject{

	public static final int HEIGHT = 65;
	public static final int WIDTH = 65;
	public static final int P_HEIGHT = 10; //%
	public static final int P_WIDTH = 10; //%
	public static final int MAX_HEALTH = 100;
	public static final int SPIN_SPEED = 250;
	public static final float TRAVEL_SPEED = 5.0f;
	public static final int UPDATE_INTERVAL = 15;
	public static final String WEAPON = "Weapon";
	public static final String FIST = "Fist";
	public static final String AXE = "Axe";
	public static final String PICKAXE = "Pickaxe";
	public static final String ROCK = "Rock";
	public static final String SPEAR = "Spear";
	public static final String SWORD = "Sword";
	public static final String TORCH = "Torch";

	Bitmap weaponImage;
	public int xPosMod = 0;
	public int yPosMod = 0;
	public int pWidth = P_WIDTH;
	public int pHeight = P_HEIGHT;
	public boolean isThrowable = false;
	public int health = MAX_HEALTH;			// duriablity
	public int damage = 25;				// per hit
	public String type = this.PICKAXE;
	public boolean isbroken = false;
	public int value = 25;				//value in currency
	public boolean flip = false;
	public boolean invincible = false;
	public boolean isHitting = false;
	public IntSize velocity = new IntSize();
	public int gravity = 8;
	public int gravityInt = 0;
	public Point targetLoc = new Point();
	public boolean tossed = false;
	private Handler handler = new Handler();

	public Weapon( Resources res, Level level, String type ){

		super(res, level);
		IntSize size = level.getPercentSize(pWidth, pHeight);
		int swidth =  (int)(size.height);
		int sheight =  (int)(size.height);
		this.type = this.PICKAXE;
		this.weaponImage = BitmapFactory.decodeResource(res, R.drawable.pickaxe);
		this.weaponImage = Bitmap.createScaledBitmap(weaponImage, swidth , sheight, true);
		this.animation = new Animation(weaponImage, x, y, swidth, sheight);
		setWidth(swidth);
		setHeight(sheight);

		setWeapon( type );

	}

	public void setWeapon( String type ){

		setX(level.player.getX() + level.player.size);
		setY(level.player.getY());
		this.invincible = false;

		if( type == this.PICKAXE ){

			this.name = PICKAXE;
			this.displayName = PICKAXE;
			this.health = MAX_HEALTH;
			this.type = this.WEAPON;
			this.isThrowable = false;
			this.damage = 25;
			this.value = 10;
			setAdjustments( level.player.size/2 + (level.player.size/5), level.player.size/5);
			pickAxe();
		}else if( type == this.AXE ){

			this.name = AXE;
			this.displayName = AXE;
			this.health = MAX_HEALTH;
			this.type = this.WEAPON;
			this.isThrowable = false;
			this.damage = 10;
			this.value = 5;
			setAdjustments( this.width, level.player.size/3);
			axe();
		}else if( type == this.TORCH ){

			this.name = TORCH;
			this.displayName = TORCH;
			this.health = MAX_HEALTH;
			this.type = this.WEAPON;
			this.isThrowable = false;
			this.damage = 5;
			this.value = 2;
			setAdjustments( this.width - (this.width/5), level.player.size/3);
			torch();
		}else if( type == this.FIST ){

			this.name = FIST;
			this.displayName = FIST;
			this.health = MAX_HEALTH;
			this.type = this.WEAPON;
			this.isThrowable = false;
			this.damage = 5;
			this.value = 0;
			this.invincible = true;
			setAdjustments( this.width - (this.width/5), level.player.size/3);
		}else if( type == this.ROCK ){

			this.name = ROCK;
			this.displayName = ROCK;
			this.health = MAX_HEALTH;
			this.type = this.WEAPON;
			this.isThrowable = true;
			this.damage = 25;
			this.value = 1;
			this.invincible = false;
			setAdjustments( this.width - (this.width/5), level.player.size/3);
			rock();
		}

	}

	public void setWeapon( InventoryItem item ){

		setX(level.player.getX());
		setY(level.player.getY());
		this.invincible = false;
		if(item.type == WEAPON){

			setWeapon( item.name );
		}else{

			this.name = item.displayName;
			this.displayName = item.displayName;
			this.health = item.health;
			this.type = Level.INVENTORY_ITEM;
			this.damage = 5;
			this.value = item.value;
			setAdjustments( level.player.size/3, level.player.size/2);
			item( item );
		}

	}

	public void pickAxe(){

		this.weaponImage = BitmapFactory.decodeResource(res, R.drawable.pickaxe);
		this.weaponImage = Bitmap.createScaledBitmap(weaponImage, width , height, true);
		this.animation = new Animation(weaponImage, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.pickaxe1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.pickaxe2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image3 = BitmapFactory.decodeResource(res, R.drawable.pickaxe3);
		image3 = Bitmap.createScaledBitmap(image3, width, height, true);

		Bitmap images[] = {weaponImage,image1,image2,image3};
		int durations[] = {0,100,200,200};

		this.animation.createAnimation(images, durations, false, 0);
	}

	public void axe(){

		this.weaponImage = BitmapFactory.decodeResource(res, R.drawable.axe);
		this.weaponImage = Bitmap.createScaledBitmap(weaponImage, width , height, true);
		this.animation = new Animation(weaponImage, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.axe1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.axe2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image3 = BitmapFactory.decodeResource(res, R.drawable.axe3);
		image3 = Bitmap.createScaledBitmap(image3, width, height, true);

		Bitmap images[] = {weaponImage,image2,image1,image2,image3};
		int durations[] = {0,100,75,50,50};

		this.animation.createAnimation(images, durations, false, 0);
	}

	public void torch(){

		this.weaponImage = BitmapFactory.decodeResource(res, R.drawable.torch);
		this.weaponImage = Bitmap.createScaledBitmap(weaponImage, width , height, true);
		this.animation = new Animation(weaponImage, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.torch1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.torch2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image3 = BitmapFactory.decodeResource(res, R.drawable.torch3);
		image3 = Bitmap.createScaledBitmap(image3, width, height, true);

		Bitmap image4 = BitmapFactory.decodeResource(res, R.drawable.torch4);
		image4 = Bitmap.createScaledBitmap(image4, width, height, true);

		Bitmap images[] = {weaponImage,image1,image2,image3,image4};
		int durations[] = {0,100,100,100,100};

		this.animation.createAnimation(images, durations, true, 0);
		this.animation.start(0, 0);
	}

	public void rock(){

		this.weaponImage = BitmapFactory.decodeResource(res, R.drawable.rockweapon);
		this.weaponImage = Bitmap.createScaledBitmap(weaponImage, width , height, true);
		this.animation = new Animation(weaponImage, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.rockweapon2);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.rockweapon3);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image3 = BitmapFactory.decodeResource(res, R.drawable.rockweapon4);
		image3 = Bitmap.createScaledBitmap(image3, width, height, true);

		Bitmap images[] = {weaponImage,image1,image2,image3};

		int durations[] = {SPIN_SPEED,SPIN_SPEED,SPIN_SPEED,SPIN_SPEED};

		this.animation.createAnimation(images, durations, true, 0);
	}

	public void item( InventoryItem item ){

		this.weaponImage = item.image;
		this.weaponImage = Bitmap.createScaledBitmap(weaponImage, this.width , this.height, true);
		this.animation = new Animation(weaponImage, x, y, this.width, this.height);
		/*
		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.axe1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.axe2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image3 = BitmapFactory.decodeResource(res, R.drawable.axe3);
		image3 = Bitmap.createScaledBitmap(image3, width, height, true);
		 */
		Bitmap images[] = {weaponImage};
		int durations[] = {0};

		this.animation.createAnimation(images, durations, false, 0);
	}

	public void setAdjustments( int x, int y ){


		this.xPosMod = x;
		this.yPosMod = y;

	}

	public void use(){

		if(this.isThrowable == false){

			animation.start(0, 0);

			if( this.invincible == false ){
				health --;
				value --;
			}

		}else{

			//throw
			//health -= 100;
			//value -=5;
		}
		if(health <= 0){
			destroy();
		}
		if(value < 0){
			value = 0;
		}
		if(type == WEAPON && name != FIST){
			level.player.inventory.get(level.player.inventory.indexOf(level.equippedItem)).health = this.health;
		}
	}

	public void throwWeapon( IntSize touchLoc ){

		if( tossed == false ){

			this.targetLoc.x = (int) touchLoc.width;
			this.targetLoc.y = (int) touchLoc.height;

			float xDistance = touchLoc.width - this.x;
			float yDistance = touchLoc.height - this.y;
			float midDistance = (xDistance + yDistance)/2;

			float speed = TRAVEL_SPEED;
			if( targetLoc.x < level.player.getX() ){
				speed *= -1;
			}
			this.velocity = new IntSize(speed, yDistance/UPDATE_INTERVAL);
			this.tossed = true;
			this.use();
			this.animation.start(0,0);
			handler.post(update);
		}
	}

	private Runnable update = new Runnable() {  

		public void run() {  

			if(tossed == true){
				x += velocity.width;
				y += velocity.height;

				velocity.height += 0.1f;


				List<ScreenObject> objslist = level.findObjectsinArea(Collision(), true);
				int camX = level.cameraX;
				for ( ScreenObject objs: objslist){
					if( objs instanceof NPC || objs instanceof Blocks ){
						Rect Col = objs.Collision();
						int left = Col.left - camX;
						int right = Col.right - camX;
						int top = Col.top;
						int bottom = Col.bottom;
						Col = new Rect(left,top,right,bottom);
						if( Rect.intersects(Col, Collision())){

							if( objs instanceof NPC ){
							objs.hit(Weapon.this, 0);
							}
							ScreenObject debris = level.effects.stoneDebris();

							debris.x = Collision().left + level.cameraX;
							debris.y = Collision().top;
							debris.width = width;
							debris.height = height;
							debris.animation.start(0, 0);
							level.screenEffects.add(debris);
							level.player.inventory.get(level.player.inventory.indexOf(level.equippedItem)).consume();
							reset();

						}
					}
				}

				//Gravity
				if(y <= level.screen_height){
					if(tossed == true){
						if( gravityInt >= gravity ){

							velocity.height +=1;
							gravityInt = 0;
						}else{

							gravityInt ++;
						}

					}

				}else{

					level.player.inventory.get(level.player.inventory.indexOf(level.equippedItem)).consume();
					reset();
				}
				handler.postDelayed(update, UPDATE_INTERVAL);

			}
		}     

	};

	public void reset(){

		this.tossed = false;
		this.velocity = new IntSize();
		this.targetLoc = new Point();
		this.gravityInt = 0;
		setX(level.player.getX()+ level.player.size);
		setY(level.player.getY());
		handler.removeCallbacks(update);
		handler = new Handler();
		this.animation.stop();
	}

	public void destroy(){

		this.health = 0;
		level.player.inventory.get(level.player.inventory.indexOf(level.equippedItem)).destroy();

	}
	public void drainHealth( int amount ){

		if( this.health - amount > 0 ){
			this.health -= amount;
		}else{

			destroy();
		}
	}
	public String getType(){

		return this.type;

	}

	@Override
	public Rect Collision(){

		Rect r = new Rect(this.x- this.width/2,this.y + (this.height/5),this.x  + this.width/2 + level.getPercentSize(1),this.y + (this.height*2));
		if(this.flip == true){
			r = new Rect(this.x - this.width*2 - level.getPercentSize(1),this.y + (this.height/10),this.x - this.width,this.y + (this.height*2));

		}
		if( tossed == true ){
			r = new Rect(this.x - xPosMod,this.y + yPosMod,this.x + this.width - xPosMod,this.y + this.height  + yPosMod);

		}
		return r;

	}

}
