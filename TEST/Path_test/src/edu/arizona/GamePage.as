package edu.arizona {
	import edu.arizona.gui.HudBar;
	
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.MouseEvent;

	public class GamePage extends Sprite {

		private static var YOUTUBE_LOGO_HEIGHT:uint = 55;

		private var yt:YouTubePlayer;
		private var bar:HudBar
		private var overlay:TouchOverlay;

		private var isCounting:Boolean;

		public function GamePage(w:uint, h:uint) {
			initYouTube(w, h);
			initOverlay(w, h);
			initHud(w, h);
		}

		private function initYouTube(w:uint, h:uint):void {
			yt = new YouTubePlayer(w, h - HudBar.HUD_HEIGHT + YOUTUBE_LOGO_HEIGHT * 2);
			yt.video.y = -YOUTUBE_LOGO_HEIGHT;
			yt.video.addEventListener("isReady", function(evt:Event):void {
				overlay.makeReady(yt.isFirstTime());
				bar.setProgress(yt.count);
				bar.setDuration(yt.duration);
			});
			yt.video.addEventListener("isNotReady", function(evt:Event):void {
				overlay.makeNotReady();
				bar.setProgress(yt.count);
			});
			yt.video.addEventListener(Event.COMPLETE, function(evt:Event):void {
				bar.finish(overlay.getCircle(), w, h);
				overlay.makeDone();
			});
			addChild(yt.video);
		}

		private function initOverlay(w:uint, h:uint):void {
			overlay = new TouchOverlay(w, h - HudBar.HUD_HEIGHT);
			addEventListener(MouseEvent.CLICK, onMouseClick);
			function onMouseClick(evt:MouseEvent):void {
				if (evt.stageY < h - HudBar.HUD_HEIGHT) {
					if (!isCounting) {
						if (yt.isFirstTime()) {
							yt.onClick();
						} else if (yt.isReady) {
							bar.undoDisabled = false;
							bar.redoDisabled = true;
							overlay.click(evt.stageX, evt.stageY);
							yt.onClick();
						}
					} else {
						overlay.click(evt.stageX, evt.stageY);
					}
				}
			}
			addChild(overlay);
		}

		private function initHud(w:uint, h:uint):void {
			bar = new HudBar(w, h, yt, overlay);
			bar.addQuitEvent(quit);
			bar.addUndoEvent(undo);
			bar.addRedoEvent(redo);
			bar.addRestartEvent(restart);
			bar.addYesEvent(quit);
			bar.addNoEvent(quit);
			bar.addFinishEvent(finishSubmit);
			addChild(bar);
		}

		public function start():void {
			isCounting = false;
			bar.prepareToTrack();
			overlay.prepareToTrack();
		}

		public function startCount():void {
			isCounting = true;
			bar.prepareToCount();
			overlay.prepareToCount();
		}

		private function undo(evt:MouseEvent):void {
			if (yt.isReady) {
				bar.undoDisabled = overlay.undo();
				bar.redoDisabled = false;
				yt.seekBack();
			}
		}

		private function redo(evt:MouseEvent):void {
			if (yt.isReady) {
				bar.redoDisabled = overlay.redo();
				bar.undoDisabled = false;
				yt.seekForward();
			}
		}

		private function restart(evt:MouseEvent = null):void {
			overlay.reload();
			bar.undoDisabled = true;
			bar.redoDisabled = true;
			bar.setProgress(0);
			bar.hideAlert();
			yt.restart();
//			if (evt != null)
//				yt.onClick();
		}

		private function quit(evt:MouseEvent):void {
			dispatchEvent(new Event("quit"));
			restart();
		}
		
		private function finishSubmit(evt:MouseEvent = null):void{
			bar.finish(overlay.getCircle(), stage.stageWidth, stage.stageHeight);
			overlay.makeDone();
		}
	}
}
