package edu.arizona {
	import com.greensock.TweenMax;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.filters.ColorMatrixFilter;
	import flash.geom.Point;
	import flash.ui.Mouse;
	
	import edu.arizona.SoundManager;
	import edu.arizona.gui.FunFactManager;
	import edu.arizona.gui.HudBar;
	import edu.arizona.util.UndoList;
	//Trim down on input later on

	public class TouchOverlay extends Sprite {

		private static const LINE_COLOR:uint = 0xFFFFFF; //0xFFD700;
		private static const LINE_THICKNESS:uint = 2;

		private static const CIRCLE_COLOR:uint = 0xFFFF00; //0x2745fc;
		private static const PEN_COLOR:uint = 0x2132EE; //0x2745fc;
		private static const CIRCLE_RADIUS:uint = 38;
		private static const CIRCLE_THICKNESS:uint = 3;

		private static const CROSS_COLOR:uint = 0xFFFF5F;
		private static const CROSS_LENGTH:uint = 18;
		private static const CROSS_THICKNESS:uint = 1;
		private static var stylize:Boolean = false;
		//private var circle:Sprite;
		private var circle:CCircle;
		private var lines:Sprite;
		private var lineCanvas:Bitmap;
		private var help:Bitmap;
		public var antID:uint;

		private var firstX:Number;
		private var firstY:Number;

		private var isCounting:Boolean;
		

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
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/pen.png")]
		private var PEN:Class;
		public var customCursor:DisplayObject = new PEN();
		private var pen_drawing:Boolean = true;
		private var sp:Sprite = new Sprite();
		//Variable to track the very last line to be drawn onto the game.
		private var lastLine:Sprite = new Sprite();

		private var lineList:UndoList;

		private var w:uint;
		private var h:uint;
		private var isFinished:Boolean = false;
		private var maxDeltaTime:int = 0;
		
		private var funFact:FunFactManager;

		private var desaturation:ColorMatrixFilter;
		private var soundMan:SoundManager;
		
		public function TouchOverlay(w:uint, h:uint, ff:FunFactManager) {
			soundMan = new SoundManager();
			this.w = w;
			this.h = h;

			this.mouseEnabled = false;
			this.mouseChildren = false;

			initCircle();
			initFirstAnt(0, 0, w, h, 0);
			initDesaturationFilter();

			lines = new Sprite();
			lineList = new UndoList();
			funFact = ff;
			
			addChild(customCursor);
			customCursor.x = -1000;
			customCursor.y = -1000;
			customCursor.visible = false;
			addChild(lastLine);
			addEventListener(Event.ENTER_FRAME, moveCursor);
		}

		private function moveCursor(event:Event):void {
			if(customCursor.visible){
				customCursor.x=mouseX;
				customCursor.y = mouseY - 37;
			}
		}
		
		private function initCircle():void {
			circle = new CCircle();
			circle.graphics.lineStyle(CIRCLE_THICKNESS, CIRCLE_COLOR, 0.75);
			if (!stylize) {
				circle.graphics.drawCircle(0, 0, CIRCLE_RADIUS);
			}else {
				circle.init();
			}
			circle.graphics.lineStyle();
			circle.graphics.beginFill(CROSS_COLOR);
			if (stylize) { circle.graphics.beginFill(PEN_COLOR); }
			circle.graphics.drawRect( -CROSS_THICKNESS / 2, -CROSS_LENGTH / 2, CROSS_THICKNESS, CROSS_LENGTH);
			circle.graphics.drawRect(-CROSS_LENGTH / 2, -CROSS_THICKNESS / 2, CROSS_LENGTH, CROSS_THICKNESS);
			circle.graphics.endFill();
			circle.visible = true;
			addChild(circle);
		}

		public function setStyle():void {
			trace("aaa");
			stylize = !stylize;
			circle.clear();
			if (!stylize) {
				customCursor.visible = false;
				circle.graphics.lineStyle(CIRCLE_THICKNESS, CIRCLE_COLOR, 0.75);
				circle.graphics.drawCircle(0, 0, CIRCLE_RADIUS);
				circle.graphics.lineStyle();
				circle.graphics.beginFill(CROSS_COLOR);
				circle.graphics.drawRect( -CROSS_THICKNESS / 2, -CROSS_LENGTH / 2, CROSS_THICKNESS, CROSS_LENGTH);
				circle.graphics.drawRect( -CROSS_LENGTH / 2, -CROSS_THICKNESS / 2, CROSS_LENGTH, CROSS_THICKNESS);
				lastLine.visible = false;
				Mouse.show();
			}else {
				customCursor.visible = true;
				lastLine.visible = true;
				circle.init();
				//Mouse.hide();
			}
			redrawLines();
		}
		
		public function getStyle():Boolean {
			return stylize;
		}
		
		private function initFirstAnt(startX:Number, startY:Number, w:uint, h:uint, startDataState:Number):void {
			/*
			   antID = Math.floor(Math.random() * FIRST_ANTS.length);
			   var p:Point = FIRST_ANTS[antID];
			 */
			if(startDataState == 0) // uses game coordinates
			{
				circle.x = startX;
				circle.y = startY;
				firstX = startX * YouTubePlayer.VID_ID1_WIDTH / YouTubePlayer.width;
				firstY = startY * YouTubePlayer.VID_ID1_HEIGHT / (YouTubePlayer.height - GamePage.YOUTUBE_LOGO_HEIGHT * 2);
			}			
			else if(startDataState == 1) // uses video coordinates
			{				
				firstX = startX;
				firstY = startY;
				circle.x = startX / YouTubePlayer.VID_ID1_WIDTH * YouTubePlayer.width;
				circle.y = startY / YouTubePlayer.VID_ID1_HEIGHT * (YouTubePlayer.height - GamePage.YOUTUBE_LOGO_HEIGHT * 2);
			}
			else
			{
				trace("Using data in an unknown way..");
			}
			
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

		public function reload(startX:Number, startY:Number, startDataState:Number):void {
			if (help.visible) {
				help.visible = false;
				removeChild(help);
			}
			initFirstAnt(startX, startY, w, h, startDataState);
			if (lineCanvas)
				removeChild(lineCanvas);
			lineCanvas = null;
			lines.graphics.clear();
			lineList = new UndoList();
		}

		public function prepareToTrack(startX:Number, startY:Number, startDataState:Number):void {
			reload(startX, startY, startDataState);
			trace("x0:" + startX + " y0:" + startY);
			isCounting = false;
		}

		public function prepareToCount():void {
			reload(0, 0, 0);
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
			if(isFinished)
				return;
			
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
						
					if(stylize){
						lines.graphics.lineStyle(LINE_THICKNESS, PEN_COLOR);
					}else {
						lines.graphics.lineStyle(LINE_THICKNESS, LINE_COLOR);
					}
					
					if(!stylize){
						lines.graphics.moveTo(circle.x, circle.y);
						lines.graphics.lineTo(cx, cy);
					}
				} else {
					//lines.graphics.lineStyle(CROSS_THICKNESS, LINE_COLOR);
					if(stylize){
						lines.graphics.lineStyle(LINE_THICKNESS, PEN_COLOR);
					}else {
						lines.graphics.lineStyle(LINE_THICKNESS, LINE_COLOR);
					}
					lines.graphics.moveTo(cx - CROSS_LENGTH / 2, cy);
					lines.graphics.lineTo(cx + CROSS_LENGTH / 2, cy);
					lines.graphics.moveTo(cx, cy - CROSS_LENGTH / 2);
					lines.graphics.lineTo(cx, cy + CROSS_LENGTH / 2);
				}

				trace("timeBefore: " + time);
				
				// ensure time % 100 = 1
				var bTime:int = time;
				trace("1:" + bTime);
				// kill lower digits
				bTime = (bTime/100)
				trace("1d:" + bTime);
				bTime *= 100;
				trace("1m:" + bTime);
				// calculate upper and lower timestamps
				var aTime:int = bTime + 100;
				trace("3:" + aTime);
				trace("4:" + bTime);
				if(Math.abs(time - aTime) < Math.abs(time - bTime))
				{
					if(Math.abs(time - aTime) > maxDeltaTime)
					{
						maxDeltaTime = Math.abs(time - aTime);						
						trace("newMaxDeltaTime: " + maxDeltaTime);
						
					}
					time = aTime + 1;
				}
				else
				{
					if(Math.abs(time - bTime) > maxDeltaTime)
					{
						maxDeltaTime = Math.abs(time - bTime);
						trace("newMaxDeltaTime: " + maxDeltaTime);
					}
					time = bTime + 1;
				}
				
				//old
				/*
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
				*/
				trace("timeAfter: " + time);
				
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

				var bdata:BitmapData = new BitmapData(w, h, true, 0x00000000);
				bdata.draw(lines, null, null, null, null, true);
				lineCanvas = new Bitmap(bdata, "auto", true);
					
				if (newClick) {					
					// this may be a bug not taking the correct player size into account
					if(time == 1 && !isCounting)
						trace("CLICK AT FIRST FRAME DETECTED!!");
					else
						lineList.add([new Point(lastX, lastY), new Point(cx, cy), time]);
					
					sp.x = circle.x;
					sp.y = circle.y;
					
					if (stylize) {
						soundMan.write_play();
						if(!isCounting){
							TweenMax.to(sp, 0.5, { x:cx, y:cy, onUpdate:drawLine, onComplete:function():void { pen_drawing = false; } } );
							lastLine.graphics.clear();
							lastLine.graphics.lineStyle(LINE_THICKNESS, PEN_COLOR);
							lastLine.graphics.moveTo(sp.x, sp.y);
							pen_drawing = true;
							circle.init();
							lines.graphics.lineStyle(LINE_THICKNESS, PEN_COLOR);
							lines.graphics.moveTo(circle.x, circle.y);
							lines.graphics.lineTo(cx, cy);
						}
					}else {
						soundMan.click_play();
					}
					
					//Temporary solution to prevent cursur from being with the pen.
					if (stylize && visible) {
						Mouse.hide();
					}
			
//					circle.x = cx;
//					circle.y = cy;
					
				}
				trace("	TIME: " + time);
				
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

		public function clearLastLine():void {
			if(lastLine){
				lastLine.graphics.clear();
			}
		}
		private function drawLine():void
		{	
			lastLine.graphics.lineTo(sp.x+Math.random()*2, sp.y+Math.random()*2-1);
			customCursor.x=sp.x;
			customCursor.y=sp.y-37;
		}
		
		/**
		 * returns whether or not the undo button should be disabled
		 */
		public function undo():Boolean {
			lastLine.graphics.clear();
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
			if(lineCanvas){
				removeChild(lineCanvas);
			}
			lines.graphics.clear();

			lineList.beginIteration();

			if(stylize){
				lines.graphics.lineStyle(LINE_THICKNESS, PEN_COLOR);
			}else {
				lines.graphics.lineStyle(LINE_THICKNESS, LINE_COLOR);
			}
				
			while (lineList.hasNext()) {
				var a:Array = lineList.next();
				var p1:Point = a[0];
				var p2:Point = a[1];
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
			var count:int = 0;
			while (tmp.hasNext() && count < 100) {
				var a:Array = tmp.next();
				var time:Number = a[2]; //Math.round(a[2] * YouTubePlayer.FPS) + 1;
				var point:Point = a[1];
				point.x = point.x * YouTubePlayer.VID_ID1_WIDTH / YouTubePlayer.width;
				point.y = point.y * YouTubePlayer.VID_ID1_HEIGHT / (YouTubePlayer.height - GamePage.YOUTUBE_LOGO_HEIGHT * 2);
				if (point.y < 0) {
					point.y = point.x = -1;
				}
				result += time + " " + point.x + " " + point.y + "\n";
				count++;
			}
			return result;
		}

		public function getSartPointsData():String {
			var result:String = "";
			lineList.beginIteration();

			while (lineList.hasNext()) {
				var a:Array = lineList.next();
				var point:Point = a[1];
				result += (point.x * YouTubePlayer.VID_ID1_WIDTH / YouTubePlayer.width) + " " + (point.y * YouTubePlayer.VID_ID1_HEIGHT / (YouTubePlayer.height - GamePage.YOUTUBE_LOGO_HEIGHT * 2)) + "\n";
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
			if(!stylize){
				if (lineCanvas)
					lineCanvas.alpha = 0.25;
				circle.filters = [desaturation];
				circle.alpha = 0.5;
				}
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
			circle.alpha = 1;*/
//			trace("makeDone");
		}
		
		public function setFinishedState(state:Boolean)
		{
			isFinished = state;
		}

		public function getCircle():DisplayObject {
			return circle;
		}
	}
}
