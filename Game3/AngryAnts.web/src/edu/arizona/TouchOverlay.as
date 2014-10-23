package edu.arizona {

	import edu.arizona.gui.FunFactManager;
	import edu.arizona.gui.HudBar;
	import edu.arizona.util.UndoList;
	
	//import fl.motion.AdjustColor;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.filters.ColorMatrixFilter;
	import flash.geom.Point;

	public class TouchOverlay extends Sprite {

		private static const LINE_COLOR:uint = 0xFFFFFF; //0xFFD700;
		private static const LINE_THICKNESS:uint = 2;

		private static const CIRCLE_COLOR:uint = 0xFFFF00; //0x2745fc;
		private static const CIRCLE_RADIUS:uint = 38;
		private static const CIRCLE_THICKNESS:uint = 3;

		private static const CROSS_COLOR:uint = 0xFFFF5F;
		private static const CROSS_LENGTH:uint = 18;
		private static const CROSS_THICKNESS:uint = 1;

		/*
		   private static const FIRST_ANTS:Array = [
		   //												 new Point(214.8548, 143.2419),
		   //												 new Point(302.9194, 134.5323),
		   //												 new Point(146.1452, 221.6290),
		   //												 new Point(145.1774, 335.8226),
		   //												 new Point(224.5323, 348.4032),
		   //												 new Point(360.9839, 299.0484),
		   //												 new Point(368.7258, 347.4355),
		   //												 new Point(370.6613, 396.7903),
		   //												 new Point(375.5000, 451.9516),
		   //												 new Point(428.7258, 447.1129),
		   //												 new Point(424.8548, 376.4677),
		   //												 new Point(451.9516, 343.5645),
		   //												 new Point(447.1129, 388.0806),
		   //												 new Point(442.2742, 183.8871),
		   //												 new Point(507.1129, 440.3387),
		   //												 new Point(503.2419, 371.6290),
		   //												 new Point(555.5000, 472.2742),
		   //												 new Point(530.3387, 417.1129),
		   //												 new Point(559.3710, 373.5645),
		   //												 new Point(524.5323, 336.7903),
		   //												 new Point(509.0484, 267.1129),
		   //												 new Point(514.8548, 203.2419),
		   //												 new Point(541.9516, 221.6290),
		   //												 new Point(516.7903, 117.1129),
		   //												 new Point(527.4355, 40.6613),
		   //												 new Point(557.4355, 67.7581),
		   //												 new Point(552.5968, 134.5323),
		   //												 new Point(614.5323, 150.0161),
		   //												 new Point(665.8226, 116.1452),
		   //												 new Point(592.2742, 302.9194),
		   //												 new Point(638.7258, 268.0806),
		   //												 new Point(617.4355, 410.3387),
		   //												 new Point(686.1452, 406.4677),
		   //												 new Point(711.3065, 468.4032),
		   //												 new Point(735.5000, 440.3387),
		   //												 new Point(713.2419, 385.1774),
		   //												 new Point(680.3387, 337.7581),
		   //												 new Point(698.7258, 282.5968),
		   //												 new Point(698.7258, 236.1452),
		   //												 new Point(747.1129, 192.5968),
		   //												 new Point(747.1129, 144.2097),
		   //												 new Point(809.0484, 134.5323),
		   //												 new Point(834.2097, 120.9839),
		   //												 new Point(833.2419, 170.3387),
		   //												 new Point(827.4355, 192.5968),
		   //												 new Point(849.6935, 313.5645),
		   //												 new Point(825.5000, 322.2742),
		   //												 new Point(770.3387, 361.9516),
		   //												 new Point(814.8548, 376.4677),
		   //												 new Point(826.4677, 420.0161)];
		   new Point(331, 280),
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
		 */
		private var circle:Sprite;
		private var lines:Sprite;
		private var lineCanvas:Bitmap;
		private var help:Bitmap;
		public var antID:uint;

		private var firstX:Number;
		private var firstY:Number;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/inst_right_down.png")]
		private var InstRightDown:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/inst_left_down.png")]
		private var InstLeftDown:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/inst_right_up.png")]
		private var InstRightUp:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/inst_left_up.png")]
		private var InstLeftUp:Class;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/inst_count.png")]
		private var InstCount:Class;

		private var lineList:UndoList;

		private var w:uint;
		private var h:uint;
		
		private var funFact:FunFactManager;

		private var desaturation:ColorMatrixFilter;

		public function TouchOverlay(w:uint, h:uint, ff:FunFactManager) {
			this.w = w;
			this.h = h;

			this.mouseEnabled = false;
			this.mouseChildren = false;

			initCircle();
			initFirstAnt(0, 0, w, h);
			initDesaturationFilter();

			lines = new Sprite();
			lineList = new UndoList();
			
			funFact = ff;
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

		private function initFirstAnt(startX:Number, startY:Number, w:uint, h:uint):void {
			/*
			   antID = Math.floor(Math.random() * FIRST_ANTS.length);
			   var p:Point = FIRST_ANTS[antID];
			 */
			firstX = startX;
			firstY = startY;
			circle.x = startX / YouTubePlayer.VID_ID1_WIDTH * YouTubePlayer.width;
			circle.y = startY / YouTubePlayer.VID_ID1_HEIGHT * (YouTubePlayer.height - GamePage.YOUTUBE_LOGO_HEIGHT * 2);
			trace("INIT: (" + circle.x + ", " + circle.y + ")	[" + (circle.x * YouTubePlayer.VID_ID1_WIDTH / YouTubePlayer.width) + ", " + circle.y * YouTubePlayer.VID_ID1_HEIGHT / (YouTubePlayer.height - GamePage.YOUTUBE_LOGO_HEIGHT * 2) + ")");
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

		public function reload(startX:Number, startY:Number):void {
			if (help.visible) {
				help.visible = false;
				removeChild(help);
			}
			initFirstAnt(startX, startY, w, h);
			if (lineCanvas)
				removeChild(lineCanvas);
			lineCanvas = null;
			lines.graphics.clear();
			lineList = new UndoList();
		}

		private var isCounting:Boolean;

		public function prepareToTrack(startX:Number, startY:Number):void {
			reload(startX, startY);
			isCounting = false;
		}

		public function prepareToCount():void {
			reload(0, 0);
			isCounting = true;
			if (help)
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

		public function click(cx:Number, cy:Number, time:Number, newClick:Boolean = true, fake:Boolean = false):void {
			if (isCounting) {
				circle.visible = true;
				help.visible = false;
			}
			if (circle.visible) {
				if (lineCanvas)
					removeChild(lineCanvas);


				if (!isCounting) {
					if (fake) {
						cx = 854 / 2;
						cy = -50;
					}
					if (circle.y < 0) {
						circle.x = 854 / 2;
						circle.y = -50;
					}
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

				// frame counts mucst always end with ~01
				var lastDigit:int = time % 10;
				var penultDigit:int = (time % 100 - lastDigit) / 10; //penultimate (2nd to last) digit
				if (lastDigit > 1) {
					if (penultDigit != 0) // time < ~01
						time = time + (11 - lastDigit);
					else // time > ~01
						time = time - lastDigit + 1;
				} else if (lastDigit == 0) {
					time++;
				}

				var lastX:Number = circle.x;
				var lastY:Number = circle.y;

				if (lastY < 0) {
					lastY = lastX = -1;
				}
				if (cy < 0) {
					circle.x = cx;
					circle.y = cy;
					cy = cx = -1;
				}

				if (newClick)
					lineList.add([new Point(lastX, lastY), new Point(cx, cy), time]);
				trace("	TIME: " + time);

				var bdata:BitmapData = new BitmapData(w, h, true, 0x00000000);
				bdata.draw(lines, null, null, null, null, true);
				lineCanvas = new Bitmap(bdata, "auto", true);
				if (!isCounting)
					lineCanvas.alpha = 0.5;
				else
					lineCanvas.alpha = 0.75;
				addChildAt(lineCanvas, 0);
			}

			if (!fake) {
				circle.x = cx;
				circle.y = cy;
				if(!isCounting)
					funFact.onClick(cx, cy);
			}
//			trace("CLICK: (" + circle.x + ", " + circle.y + ")	[" + (circle.x * YouTubePlayer.VID_ID1_WIDTH / YouTubePlayer.width) + ", " + circle.y * YouTubePlayer.VID_ID1_HEIGHT / (YouTubePlayer.height - GamePage.YOUTUBE_LOGO_HEIGHT * 2) + ")");
//			trace(circle.x, circle.y);
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
				click(p.x, p.y, a[2], false);
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

		public function getAntPathData():String {
			var result:String = "";

			// reverse list order
			var tmp:UndoList = new UndoList();
			lineList.beginIteration();
			while (lineList.hasNext()) {
				tmp.add(lineList.next());
			}

			// output results
			result += 1 + " " + firstX + " " + firstY + "\n";
			tmp.beginIteration();
			while (tmp.hasNext()) {
				var a:Array = tmp.next();
				var time:Number = a[2]; //Math.round(a[2] * YouTubePlayer.FPS) + 1;
				var point:Point = a[1];
				point.x = point.x * YouTubePlayer.VID_ID1_WIDTH / YouTubePlayer.width;
				point.y = point.y * YouTubePlayer.VID_ID1_HEIGHT / (YouTubePlayer.height - GamePage.YOUTUBE_LOGO_HEIGHT * 2);
				if (point.y < 0) {
					point.y = point.x = -1;
				}
				result += time + " " + point.x + " " + point.y + "\n";
			}
			return result;
		}

		public function getSartPointsData():String {
			var result:String = "";
			lineList.beginIteration();

			while (lineList.hasNext()) {
				var a:Array = lineList.next();
				var point:Point = a[1];
				result += point.x + " " + point.y + "\n";
			}
//			if(p1)
//				result += 0 + "/" + p1.x + "/" + p1.y + "\n";
			return result;
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
			circle.filters = [desaturation];
			circle.alpha = 0.5;
			if (help.visible) {
				help.visible = false;
				removeChild(help);
			}
		}
		
		public function makeDone():void {
			/*
			var ac:AdjustColor = new AdjustColor();
			ac.hue = 50;
			ac.brightness = 50;
			ac.contrast = 50;
			ac.saturation = 50;
			var green:ColorMatrixFilter = new ColorMatrixFilter(ac.CalculateFinalFlatArray());
			circle.filters = [green];
			circle.alpha = 1;*/
//			trace("makeDone");
		}

		public function getCircle():DisplayObject {
			return circle;
		}
	}
}
