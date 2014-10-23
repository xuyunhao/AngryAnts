package edu.arizona.gui {
	import flash.display.Sprite;

	public class ProgressBar extends Sprite {

		private static const MARGIN_LEFT:uint = 10;
		private static const MARGIN_TOP:uint = 5;
		private static const WIDTH:Number = 304;
		private static const HEIGHT:Number = 18;

		private static const BAR_BORDER_THICKNESS:uint = 1;
		private static const BAR_BORDER_COLOR:uint = 0x737373;
		private static const BAR_BG_COLOR:uint = 0xa8a8a8;
		private static const BAR_PROGRESS_COLOR:uint = 0x048004;

		private var probar:Sprite;
		private var max:Number;

		public function ProgressBar(max:Number) {
			this.max = max;
			initBar();
		}

		private function initBar():void {
			var bgbar:Sprite = new Sprite();
			bgbar.graphics.lineStyle(BAR_BORDER_THICKNESS, BAR_BORDER_COLOR);
			bgbar.graphics.beginFill(BAR_BG_COLOR);
			bgbar.graphics.drawRect(MARGIN_LEFT, MARGIN_TOP, WIDTH, HEIGHT);
			bgbar.graphics.endFill();
			addChild(bgbar);

			probar = new Sprite();
			probar.graphics.lineStyle();
			probar.graphics.beginFill(BAR_PROGRESS_COLOR);
			probar.graphics.drawRect(0, 0, WIDTH, HEIGHT);
			probar.graphics.endFill();
			probar.width = 1;
			probar.x = MARGIN_LEFT;
			probar.y = MARGIN_TOP;
			addChild(probar);
		}

		public function set progress(v:Number):void {
			probar.width = v / max * WIDTH;
		}

		public function set duration(v:Number):void {
			max = Math.floor(v);
		}

		public function isDone():Boolean {
			return (probar.width >= WIDTH);
		}
	}
}
