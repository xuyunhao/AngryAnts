package edu.arizona {

	import edu.arizona.gui.HudBar;
	import edu.arizona.util.UndoList;

	//import fl.motion.AdjustColor;
	import flash.media.*; 
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.filters.ColorMatrixFilter;
	import flash.geom.Point;
	import flash.utils.Timer;

	public class TouchOverlay extends Sprite {

		private var LINE_COLOR:uint = 0xFFFFFF; //0xFFD700;
		private static const LINE_THICKNESS:uint = 5;

		private var CIRCLE_COLOR:uint = 0xFFFF00; //0x2745fc;
		private static const CIRCLE_RADIUS:uint = 38;
		private static const CIRCLE_THICKNESS:uint = 5;
		
		private var CROSS_COLOR:uint = 0xFFFF5F;
		private static const CROSS_LENGTH:uint = 18;
		private static const CROSS_THICKNESS:uint = 3;

		private static const FIRST_ANTS:Array = [new Point(331, 280),
												 new Point(192, 133),
												 new Point(288, 120),
												 new Point(201, 326),
												 new Point(404, 169),
												 new Point(333, 318),
												 new Point(386, 342),
												 new Point(470, 190),
												 new Point(475, 109),
												 new Point(509, 120),
												 new Point(554, 135),
												 new Point(621, 307),
												 new Point(575, 375),
												 new Point(463, 245),
												 new Point(502, 206),
												 new Point(610, 103),
												 new Point(685, 180),
												 new Point(636, 222),
												 new Point(638, 260),
												 new Point(707, 336),
												 new Point(757, 301),
												 new Point(758, 392),
												 new Point(754, 345),
												 new Point(624, 375),
												 new Point(664, 353),
												 new Point(504, 345),
												 new Point(481, 310),
												 new Point(416, 318),
												 new Point(417, 353),
												 new Point(386, 412),
												 new Point(488, 386),
												 new Point(488, 386),
												 new Point(466, 406),
												 new Point(455, 349),
												 new Point(692, 126),
												 new Point(337, 371)]

		private var circle:Sprite;
		private var lines:Sprite;
		private var lineCanvas:Bitmap;
		private var help:Bitmap;

		[Embed(source = "menu/inst_right_down.png")]
		private var InstRightDown:Class;
		[Embed(source = "menu/inst_left_down.png")]
		private var InstLeftDown:Class;
		[Embed(source = "menu/inst_right_up.png")]
		private var InstRightUp:Class;
		[Embed(source = "menu/inst_left_up.png")]
		private var InstLeftUp:Class;
		
		[Embed(source = "menu/inst_count.png")]
		private var InstCount:Class;
		
		[Embed(source = "sounds/crumple.mp3")]
		private var sndCls:Class;
        public var snd:Sound = new sndCls() as Sound; 
		
		
		[Embed(source = "sounds/drip.mp3")]
		private var sndCls2:Class;
        public var snd2:Sound = new sndCls2() as Sound; 
		
        public var sndChannel:SoundChannel;
		private var lineList:UndoList;
		private var w:uint;
		private var h:uint;
		private var sizeFactor = -1.9;
		//(x^2+1)/(x^2/4+x+1)
		//From 2 to 40, peaks at 4
		private var desaturation:ColorMatrixFilter;

		//Z addition
		public static const SHAKE:Boolean = false;
		public static const SHAKE_TIMES:int = 20;
		public static const SHAKE_INTERVAL:int = 400;
		public static const SHAKE_WIDTH:int = 10;
		public var counter:int = 0;
		public var myTimer:Timer;
		public var temp_circle_x:int;	
		public var temp_circle_y:int;	
		
		public static const DRIP:Boolean = true;
		public static const DRIP_TIMES:int = 60;
		public static const DRIP_INTERVAL:int = 500;
		public static const DRIP_START_WIDTH:int = 8;
		public static const DRIP_FINAL_WIDTH:int = 12;
		public static const DRIP_START_RADIUS:int = 1;
		public static const DRIP_FINAL_RADIUS:int = 8;
		public var counter2:int = 0;
		public var myTimer2:Timer;
		
		public function TouchOverlay(w:uint, h:uint) {
			this.w = w;
			this.h = h;

			this.mouseEnabled = false;
			this.mouseChildren = false;

			if (DRIP) {
					CROSS_COLOR = 0x0044DD;
					LINE_COLOR = 0x0044DD;
					CIRCLE_COLOR = 0x0044DD;
				}
			initCircle();
			initFirstAnt(w, h);
			initDesaturationFilter();
	
			lines = new Sprite();
			lineList = new UndoList();
	
			//Z's additions
			myTimer = new Timer(SHAKE_INTERVAL / SHAKE_TIMES, SHAKE_TIMES);
			myTimer.addEventListener(TimerEvent.TIMER, timerListener);
			myTimer2 = new Timer(DRIP_INTERVAL / DRIP_TIMES, DRIP_TIMES);
			myTimer2.addEventListener(TimerEvent.TIMER, timerListener2);
		}

		private function initCircle():void {
			circle = new Sprite();
			circle.graphics.lineStyle(CIRCLE_THICKNESS, CIRCLE_COLOR, 0.75);
			circle.graphics.drawCircle(0, 0, CIRCLE_RADIUS);
			circle.graphics.lineStyle();
			circle.graphics.beginFill(CROSS_COLOR);
			circle.graphics.drawRect(-CROSS_THICKNESS / 2, -CROSS_LENGTH / 2, CROSS_THICKNESS, CROSS_LENGTH);
			circle.graphics.endFill() //avoid overlap, which creates negative space in the drawing
			circle.graphics.beginFill(CROSS_COLOR);
			circle.graphics.drawRect(-CROSS_LENGTH / 2, -CROSS_THICKNESS / 2, CROSS_LENGTH, CROSS_THICKNESS);
			circle.graphics.endFill();
			circle.visible = false;
			addChild(circle);
		}

		private function initFirstAnt(w:uint, h:uint):void {
			var i:int = Math.floor(Math.random() * FIRST_ANTS.length);
			var p:Point = FIRST_ANTS[i];
			circle.x = p.x;
			circle.y = p.y;
			circle.visible = true;

			var useRight:Boolean = false;
			var useDown:Boolean = false;
			if (circle.x > w / 2)
				useRight = true;
			if (circle.y > (h - HudBar.HUD_HEIGHT) / 2)
				useDown = true;
			if (useRight && useDown) {
				help = new InstRightDown();
				help.x = circle.x - 139;
				help.y = circle.y - circle.height - help.height + 40;
			} else if (!useRight && useDown) {
				help = new InstLeftDown();
				help.x = circle.x - 45;
				help.y = circle.y - circle.height - help.height + 40;
			} else if (useRight && !useDown) {
				help = new InstRightUp();
				help.x = circle.x - 138;
				help.y = circle.y + circle.height - 40;
			} else if (!useRight && !useDown) {
				help = new InstLeftUp();
				help.x = circle.x - 45;
				help.y = circle.y + circle.height - 40;
			}
			addChild(help);
		}

		public function reload():void {
			if (help.visible) {
				help.visible = false;
				removeChild(help);
			}
			initFirstAnt(w, h);
			if (lineCanvas)
				removeChild(lineCanvas);
			lineCanvas = null;
			lines.graphics.clear();
			lineList = new UndoList();
		}

		private var isCounting:Boolean;

		public function prepareToTrack():void {
			isCounting = false;
		}

		public function prepareToCount():void {
			isCounting = true;
			if(help)
				help.visible = false;
			help = new InstCount();
			help.x = 355;
			help.y = 231;
			addChild(help);
			circle.visible = false;
		}

		private function initDesaturationFilter():void {
			var r:Number = 0.212671;
			var g:Number = 0.71516;
			var b:Number = 0.072169;

			var m:Array = new Array();
			m = m.concat([r, g, b, 0, 0]); // red
			m = m.concat([r, g, b, 0, 0]); // green
			m = m.concat([r, g, b, 0, 0]); // blue
			m = m.concat([0, 0, 0, 1, 0]); // alpha

			desaturation = new ColorMatrixFilter(m);
		}

		public function click(cx:Number, cy:Number, newClick:Boolean = true):void {
			if(isCounting){
				circle.visible = true;
				help.visible = false;
			}
			if (circle.visible) {
				if (lineCanvas)
					removeChild(lineCanvas);

				if (!isCounting) {
					lines.graphics.lineStyle(LINE_THICKNESS, LINE_COLOR);
					lines.graphics.moveTo(circle.x, circle.y);
					lines.graphics.lineTo(cx, cy);
				} else {
					lines.graphics.lineStyle(CROSS_THICKNESS, LINE_COLOR);
					lines.graphics.moveTo(cx - CROSS_LENGTH / 2, cy);
					lines.graphics.lineTo(cx + CROSS_LENGTH / 2, cy);
					lines.graphics.moveTo(cx, cy - CROSS_LENGTH / 2);
					lines.graphics.lineTo(cx, cy + CROSS_LENGTH / 2);
				}
				if (newClick){
					lineList.add([new Point(circle.x, circle.y), new Point(cx, cy)]);
					if(SHAKE){
					circle_shake();}
					if(DRIP){
					circle_drip();}
				}
				var bdata:BitmapData = new BitmapData(w, h, true, 0x00000000);
				bdata.draw(lines, null, null, null, null, true);
				lineCanvas = new Bitmap(bdata, "auto", true);
				if(!isCounting)
					lineCanvas.alpha = 0.5;
				else
					lineCanvas.alpha = 0.75;
				addChildAt(lineCanvas, 0);
			}

			circle.x = cx;
			circle.y = cy;
			temp_circle_x = cx;
			temp_circle_y = cy;
			trace(circle.x, circle.y);
		}

		public function circle_shake(): void {
			sndChannel = snd.play();
			myTimer.start();
		}
		
		public function timerListener (e:TimerEvent):void {
			counter++;
			circle.x = temp_circle_x + SHAKE_WIDTH - Math.random() * SHAKE_WIDTH * 2;
			if (counter == SHAKE_TIMES) { 
				circle.x = temp_circle_x;
				counter = 0;
				myTimer.reset();
				myTimer.stop(); 
			}
			trace("Timer is Triggered, counter = "+ counter);
		}
		
		public function circle_drip(): void {
			sndChannel = snd2.play();
			myTimer2.start();
		}
		
		public function timerListener2 (e:TimerEvent):void {
			counter2++;
			circle.scaleX += 0.13;
			circle.scaleY += 0.13;
			circle.alpha -= 0.01;
			if (counter2 == DRIP_TIMES) { 
				counter2 = 0;
				circle.scaleX = 1;
				circle.scaleY = 1;
				circle.alpha = 1;
				myTimer2.reset();
				myTimer2.stop(); 
			}
			trace("Timer2 is Triggered, counter = "+ counter);
		}
		
		/**
		 * returns whether or not the undo button should be disabled
		 */
		public function undo():Boolean {
			if (lineList.canUndo()) {
				var a:Array = lineList.undo();
				var p:Point = a[0];
				circle.x = p.x;
				circle.y = p.y;
				redrawLines();
				if (lineList.canUndo()) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		}

		/**
		 * returns whether or not the redo button should be disabled
		 */
		public function redo():Boolean {
			if (lineList.canRedo()) {
				var a:Array = lineList.redo();
				var p:Point = a[1];
				click(p.x, p.y, false);
				if (lineList.canRedo()) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		}

		private function redrawLines():void {
			removeChild(lineCanvas);
			lines.graphics.clear();

			lineList.beginIteration();

			while (lineList.hasNext()) {
				var a:Array = lineList.next();
				var p1:Point = a[0];
				var p2:Point = a[1];

				lines.graphics.lineStyle(5, LINE_COLOR);
				lines.graphics.moveTo(p1.x, p1.y);
				lines.graphics.lineTo(p2.x, p2.y);
			}

			var bdata:BitmapData = new BitmapData(w, h, true, 0x00000000);
			bdata.draw(lines, null, null, null, null, true);
			lineCanvas = new Bitmap(bdata, "auto", true);
			lineCanvas.alpha = 0.5;
			addChildAt(lineCanvas, 0);
		}

		public function makeReady(showHelp:Boolean):void {
			if (lineCanvas)
				lineCanvas.alpha = 0.5;
			circle.filters = [];
			circle.alpha = 1;
		}

		public function makeNotReady():void {
			if (lineCanvas)
				lineCanvas.alpha = 0.25;
			//Z's addition
			if(!DRIP){
				circle.filters = [desaturation];
			}else {
				
			}
			circle.alpha = 0.5;
			if (help.visible) {
				help.visible = false;
				removeChild(help);
			}
		}

		public function makeDone():void {
			/*var ac:AdjustColor = new AdjustColor();
			ac.hue = 50;
			ac.brightness = 50;
			ac.contrast = 50;
			ac.saturation = 50;
			var green:ColorMatrixFilter = new ColorMatrixFilter(ac.CalculateFinalFlatArray());
			circle.filters = [green];
			circle.alpha = 1;
			trace("makeDone");*/
		}

		public function getCircle():DisplayObject {
			return circle;
		}
	}
}
