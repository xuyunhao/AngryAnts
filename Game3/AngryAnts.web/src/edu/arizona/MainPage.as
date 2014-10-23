package edu.arizona {
	import edu.arizona.gui.Button3State;
	
	import flash.display.Bitmap;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.MouseEvent;

	public class MainPage extends Sprite {

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/logo.png")]
		private var AngryAntsLogo:Class;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/start_up.png")]
		private var StartButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/start_roll.png")]
		private var StartButton_OVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/start_down.png")]
		private var StartButton_DOWN:Class;
		
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/count_up.png")]
		private var CountButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/count_roll.png")]
		private var CountButton_OVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/count_down.png")]
		private var CountButton_DOWN:Class;
		
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/ants1.png")]
		private var AntImg1:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/ants2.png")]
		private var AntImg2:Class;
		
		private var ant1:Bitmap;
		private var ant2:Bitmap;

		public function MainPage(w:uint, h:uint) {
			initMenu(w, h);
			initAntImgs();
			initBorder(w, h);
		}

		private function initMenu(w:uint, h:uint):void {
			var logo:Bitmap = new AngryAntsLogo();
			logo.x = w / 2 - logo.width / 2;
			logo.y = 104;
			addChild(logo);

			var btn:Button3State = new Button3State(new StartButton_UP(),
													new StartButton_OVER(),
													new StartButton_DOWN());
			btn.x = w / 2 - btn.width / 2;
			btn.y = 280;
			btn.addEventListener(MouseEvent.CLICK, onStart, false, 0, true);
			addChild(btn);
			
			var btn2:Button3State = new Button3State(new CountButton_UP(),
				new CountButton_OVER(),
				new CountButton_DOWN());
			btn2.x = w / 2 - btn2.width / 2;
			btn2.y = 379;
			btn2.addEventListener(MouseEvent.CLICK, onStartCount, false, 0, true);
			addChild(btn2);
		}
		
		private function onStart(evt:MouseEvent):void{
			dispatchEvent(new Event("start"));
		}
		
		private function onStartCount(evt:MouseEvent):void{
			dispatchEvent(new Event("start-count"));
		}
		
		private function initAntImgs():void{
			ant1 = new AntImg1();
			ant1.x = 584;
			ant1.y = 374;
			addChild(ant1);
			
			ant2 = new AntImg2();
			ant2.x = 0;
			ant2.y = 217;
			addChild(ant2);
		}

		private function initBorder(w:uint, h:uint):void {
			var stroke:Sprite = new Sprite();
			stroke.graphics.lineStyle();
			stroke.graphics.beginFill(0);
			stroke.graphics.drawRect(0, 0, w, 1); //top
			stroke.graphics.drawRect(w - 1, 0, 1, h); //right
			stroke.graphics.drawRect(0, h - 1, w, 1); //botoom
			stroke.graphics.drawRect(0, 0, 1, h); //left
			stroke.graphics.endFill();
			addChild(stroke);
		}
	}
}
