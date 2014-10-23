package edu.arizona.gui {
	import edu.arizona.TouchOverlay;
	import edu.arizona.YouTubePlayer;
	
	import flash.display.Bitmap;
	import flash.display.DisplayObject;
	import flash.display.GradientType;
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.geom.Matrix;

	public class HudBar extends Sprite {

		public static var HUD_HEIGHT:uint = 30;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/undo_up.png")]
		private var UndoButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/undo_disable.png")]
		private var UndoButton_DISABLE:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/undo_hover.png")]
		private var UndoButton_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/undo_down.png")]
		private var UndoButton_DOWN:Class;

		private var undo:Button4State;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/redo_up.png")]
		private var RedoButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/redo_disable.png")]
		private var RedoButton_DISABLE:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/redo_hover.png")]
		private var RedoButton_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/redo_down.png")]
		private var RedoButton_DOWN:Class;

		private var redo:Button4State;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/1x_up.png")]
		private var x1_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/1x_hover.png")]
		private var x1_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/1x_down.png")]
		private var x1_DOWN:Class;

		private var x1:ButtonSpeedControl;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/2x_up.png")]
		private var x2_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/2x_hover.png")]
		private var x2_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/2x_down.png")]
		private var x2_DOWN:Class;

		private var x2:ButtonSpeedControl;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/4x_up.png")]
		private var x4_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/4x_hover.png")]
		private var x4_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/4x_down.png")]
		private var x4_DOWN:Class;

		private var x4:ButtonSpeedControl;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/showhide_up.png")]
		private var ShowHideButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/showhide_disabled.png")]
		private var ShowHideButton_DISABLE:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/showhide_hover.png")]
		private var ShowHideButton_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/showhide_down.png")]
		private var ShowHideButton_DOWN:Class;

		private var showhide:Button4State;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/restart_up.png")]
		private var RestartButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/restart_hover.png")]
		private var RestartButton_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/restart_down.png")]
		private var RestartButton_DOWN:Class;

		private var restart:Button3State;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/quit_up.png")]
		private var QuitButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/quit_hover.png")]
		private var QuitButton_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/quit_down.png")]
		private var QuitButton_DOWN:Class;

		private var quit:Button3State;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/finish_up.png")]
		private var FinishButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/finish_hover.png")]
		private var FinishButton_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/finish_down.png")]
		private var FinishButton_DOWN:Class;

		private var finishBtn:Button3State;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/done.png")]
		private var DoneImage:Class;

		private var alert:Sprite;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/yes_up.png")]
		private var YesButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/yes_hover.png")]
		private var YesButton_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/yes_down.png")]
		private var YesButton_DOWN:Class;

		private var yes:Button3State;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/no_up.png")]
		private var NoButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/no_hover.png")]
		private var NoButton_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/no_down.png")]
		private var NoButton_DOWN:Class;

		private var no:Button3State;

		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/offscreen_up.png")]
		private var OffscreenButton_UP:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/offscreen_hover.png")]
		private var OffscreenButton_HOVER:Class;
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/menu/offscreen_down.png")]
		private var OffscreenButton_DOWN:Class;

		private var offscreen:Button3State;

		private var progress:ProgressBar;
		private var yt:YouTubePlayer;
		private var to:TouchOverlay;
		private var facts:FunFactManager;

		public function HudBar(w:uint, h:uint, yt:YouTubePlayer, to:TouchOverlay, f:FunFactManager) {
			this.yt = yt;
			this.to = to;
			this.facts = f;
			drawBar(w, h);
			initButtons(w, h);
			initAlert(w, h);
		}

		private function drawBar(w:uint, h:uint):void {
			var colors:Array = [0xFFFFFF, 0xcbcbcb];
			var alphas:Array = [1, 1];
			var ratios:Array = [0, 255];
			var m:Matrix = new Matrix();
			m.createGradientBox(w, HUD_HEIGHT, Math.PI / 2);

			var hudBG:Sprite = new Sprite();
			hudBG.y = h - HUD_HEIGHT;
			hudBG.graphics.lineStyle();
			hudBG.graphics.beginGradientFill(GradientType.LINEAR, colors, alphas, ratios, m);
			hudBG.graphics.drawRect(0, 0, w, HUD_HEIGHT);
			hudBG.graphics.endFill();
			hudBG.graphics.beginFill(0xb4b4b4);
			hudBG.graphics.drawRect(0, HUD_HEIGHT - 1, w, 1);
			hudBG.graphics.endFill();
			addChild(hudBG);
		}

		private function initButtons(w:uint, h:uint):void {
			undo = new Button4State(new UndoButton_UP(),
									new UndoButton_HOVER(),
									new UndoButton_DOWN(),
									new UndoButton_DISABLE());
			undo.isDisabled = true;
			undo.x = 0;
			undo.y = h - HUD_HEIGHT;
			addChild(undo);

			redo = new Button4State(new RedoButton_UP(),
									new RedoButton_HOVER(),
									new RedoButton_DOWN(),
									new RedoButton_DISABLE());
			redo.isDisabled = true;
			redo.x = undo.width;
			redo.y = h - HUD_HEIGHT;
			addChild(redo);

			quit = new Button3State(new QuitButton_UP(),
									new QuitButton_HOVER(),
									new QuitButton_DOWN());
			quit.x = w - quit.width;
			quit.y = h - HUD_HEIGHT;
			addChild(quit);

			restart = new Button3State(new RestartButton_UP(),
									   new RestartButton_HOVER(),
									   new RestartButton_DOWN());
			restart.x = quit.x - restart.width;
			restart.y = h - HUD_HEIGHT;
			addChild(restart);

			finishBtn = new Button3State(new FinishButton_UP(),
										 new FinishButton_HOVER(),
										 new FinishButton_DOWN());
			finishBtn.x = restart.x - finishBtn.width;
			finishBtn.y = h - HUD_HEIGHT;
			addChild(finishBtn);

			x1 = new ButtonSpeedControl(new x1_UP(),
										new x1_HOVER(),
										new x1_DOWN());
			x1.x = redo.x + redo.width;
			x1.y = redo.y;
			ButtonSpeedControl.makeSelected(x1);
			x1.addEventListener(MouseEvent.CLICK, onSpeedClick, false, 0, true);
			addChild(x1);

			x2 = new ButtonSpeedControl(new x2_UP(),
										new x2_HOVER(),
										new x2_DOWN());
			x2.x = x1.x + x1.width;
			x2.y = x1.y;
			x2.addEventListener(MouseEvent.CLICK, onSpeedClick, false, 0, true);
			addChild(x2);

			x4 = new ButtonSpeedControl(new x4_UP(),
										new x4_HOVER(),
										new x4_DOWN());
			x4.x = x2.x + x2.width;
			x4.y = x2.y;
			x4.addEventListener(MouseEvent.CLICK, onSpeedClick, false, 0, true);
			addChild(x4);

			showhide = new Button4State(new ShowHideButton_UP(),
										new ShowHideButton_HOVER(),
										new ShowHideButton_DOWN(),
										new ShowHideButton_DISABLE(), true);
			showhide.x = x4.x + x4.width;
			showhide.y = x4.y;
			showhide.addEventListener(MouseEvent.CLICK, onShowHideClick, false, 0, true);
			showhide.isDisabled = false;
			addChild(showhide);
			
			var bg:Sprite = new Sprite();
			bg.graphics.beginFill(0xFFFFFF);
			bg.graphics.drawRect(0, -50, 900, 50);
			bg.graphics.endFill();
			addChild(bg);

			offscreen = new Button3State(new OffscreenButton_UP(),
										 new OffscreenButton_HOVER(),
										 new OffscreenButton_DOWN());
			offscreen.x = 0;
			offscreen.y = -50;
			addChild(offscreen);

			progress = new ProgressBar(69);
			progress.x = showhide.x + showhide.width;
			progress.y = showhide.y;
			addChild(progress);
		}

		private function onSpeedClick(evt:MouseEvent):void {
			ButtonSpeedControl.makeSelected(evt.target as ButtonSpeedControl);
			if (evt.target == x1) {
				yt.setSpeed1x();
			} else if (evt.target == x2) {
				yt.setSpeed2x();
			} else if (evt.target == x4) {
				yt.setSpeed4x();
			}
		}

		private function onShowHideClick(evt:MouseEvent):void {
			to.visible = showhide.isDisabled;
			facts.hide = !facts.hide;
			showhide.isDisabled = !showhide.isDisabled;
		}

		private function initAlert(w:uint, h:uint):void {
			alert = new Sprite();

			var done:Bitmap = new DoneImage();
			alert.addChild(done);

			yes = new Button3State(new YesButton_UP(),
								   new YesButton_HOVER(),
								   new YesButton_DOWN());
			yes.y = done.height;
			alert.addChild(yes);

			no = new Button3State(new NoButton_UP(),
								  new NoButton_HOVER(),
								  new NoButton_DOWN());
			no.x = yes.width;
			no.y = yes.y;
			alert.addChild(no);

			alert.x = w / 2 - alert.width / 2;
			alert.y = h / 2 - alert.height / 2;
			alert.visible = false;
		}

		public function setProgress(v:Number):Boolean {
			progress.progress = v;
			return progress.isDone();
		}

		public function setDuration(v:Number):void {
			progress.duration = v;
		}

		public function addQuitEvent(f:Function):void {
			quit.addEventListener(MouseEvent.CLICK, f, false, 0, true);
		}

		public function addUndoEvent(f:Function):void {
			undo.addEventListener(MouseEvent.CLICK, f, false, 0, true);
		}

		public function addRedoEvent(f:Function):void {
			redo.addEventListener(MouseEvent.CLICK, f, false, 0, true);
		}

		public function addRestartEvent(f:Function):void {
			restart.addEventListener(MouseEvent.CLICK, f, false, 0, true);
		}

		public function addYesEvent(f:Function):void {
			yes.addEventListener(MouseEvent.CLICK, f, false, 0, true);
		}

		public function addNoEvent(f:Function):void {
			no.addEventListener(MouseEvent.CLICK, f, false, 0, true);
		}

		public function addFinishEvent(f:Function):void {
			finishBtn.addEventListener(MouseEvent.CLICK, f, false, 0, true);
		}

		public function set undoDisabled(b:Boolean):void {
			undo.isDisabled = b;
		}

		public function get undoDisabled():Boolean {
			return undo.isDisabled;
		}

		public function set redoDisabled(b:Boolean):void {
			redo.isDisabled = b;
		}

		public function get redoDisabled():Boolean {
			return redo.isDisabled;
		}

		public function prepareToCount():void {
			progress.visible = false;
			x1.visible = false;
			x2.visible = false;
			x4.visible = false;
			finishBtn.visible = true;
			showhide.x = redo.x + redo.width;
			offscreen.visible = false;
			showOverlay();
		}

		public function prepareToTrack():void {
			progress.visible = true;
			x1.visible = true;
			x2.visible = true;
			x4.visible = true;
			finishBtn.visible = false;
			showhide.x = x4.x + x4.width;
			offscreen.visible = true;
			showOverlay();
		}

		public function showOverlay():void {
			to.visible = true;
			showhide.isDisabled = false;
		}

		public function finish(circle:DisplayObject, w:uint, h:uint):void {
			var useRight:Boolean = false;
			var useDown:Boolean = false;
			if (circle.x > w / 2)
				useRight = true;
			if (circle.y > (h - HudBar.HUD_HEIGHT) / 2)
				useDown = true;
			if (useRight && useDown) {
				alert.x = circle.x - 139;
				alert.y = circle.y - circle.height - alert.height + 40;
			} else if (!useRight && useDown) {
				alert.x = circle.x - 45;
				alert.y = circle.y - circle.height - alert.height + 40;
			} else if (useRight && !useDown) {
				alert.x = circle.x - 138;
				alert.y = circle.y + circle.height - 40;
			} else if (!useRight && !useDown) {
				alert.x = circle.x - 45;
				alert.y = circle.y + circle.height - 40;
			}

			alert.visible = true;
			addChild(alert);
		}

		public function hideAlert():void {
			if (alert.visible) {
				alert.visible = false;
				removeChild(alert);
			}
		}
	}
}
