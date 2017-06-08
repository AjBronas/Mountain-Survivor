package helium.games.survivor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;

public class Effects {
	public static final int P_HEIGHT = 10; //%
	public static final int P_WIDTH = 10; //%

	public int pWidth = P_WIDTH;;
	public int pHeight = P_HEIGHT;
	public int width = 50;
	public int height = 50;
	Resources res;
	Level level;
	public Effects( Resources res, Level level){

		this.res = res;
		this.level = level;
		IntSize size = level.getPercentSize(pWidth,pHeight );
		width =  (int)(size.width);
		height =  (int)(size.height);
 
	}

	public ScreenObject woodDebris(){

		IntSize size = level.getPercentSize(pWidth,pHeight );
		int width =  (int)(size.width);
		int height =  (int)(size.height);
		ScreenObject debris = new ScreenObject(res, level);


		Bitmap chip = BitmapFactory.decodeResource(res, R.drawable.woodchip1);
		chip = Bitmap.createScaledBitmap(chip, width, height, true);

		debris.animation = new Animation(chip, level.player.weapon.x, level.player.weapon.y, width, height);
		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.woodchip1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.woodchip2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image3 = BitmapFactory.decodeResource(res, R.drawable.woodchip3);
		image3 = Bitmap.createScaledBitmap(image3, width, height, true);

		Bitmap images[] = {chip,image1,image2,image3};
		int durations[] = {5,100,100,100};

		debris.animation.createAnimation(images, durations, false, 0);
		return debris;
	}

	public ScreenObject stoneDebris(){

		IntSize size = level.getPercentSize(pWidth,pHeight );
		int width =  (int)(size.width);
		int height =  (int)(size.height);
		ScreenObject debris = new ScreenObject(res, level);


		Bitmap chip = BitmapFactory.decodeResource(res, R.drawable.rockchip1);
		chip = Bitmap.createScaledBitmap(chip, width, height, true);

		debris.animation = new Animation(chip, level.player.weapon.x, level.player.weapon.y, width, height);
		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.rockchip1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.rockchip2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image3 = BitmapFactory.decodeResource(res, R.drawable.rockchip3);
		image3 = Bitmap.createScaledBitmap(image3, width, height, true);

		Bitmap images[] = {chip,image1,image2,image3};
		int durations[] = {5,75,75,75};

		debris.animation.createAnimation(images, durations, false, 0);
		return debris;
	}

	public ScreenObject blood(){
		

		ScreenObject debris = new ScreenObject(res, level);


		Bitmap blood = BitmapFactory.decodeResource(res, R.drawable.blood);
		blood = Bitmap.createScaledBitmap(blood, width, height, true);

		debris.animation = new Animation(blood, level.player.weapon.x, level.player.weapon.y, width, height);
		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.blood1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.blood2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap images[] = {blood,image1,image2};
		int durations[] = {105,75,75};

		debris.animation.createAnimation(images, durations, false, 0);

		return debris;
	}

}
