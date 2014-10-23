package edu.arizona 
{
    import flash.display.Sprite;
    import flash.events.Event;
    
    public class CCircle extends Sprite
    {
        private var limit:Number;
		private var speed:Number;
		private var radius:Number;
		private var deg2rad:Number;
		private var thickness:int;
		private var circle:Sprite;//Circle has a circle object! That's why.
		private var spd:Number;
		private var start:Number;
		private var end:Number;
		
        public function CCircle():void
        {
            //init();
            addEventListener(Event.ENTER_FRAME, draw_circle);
        }
        
        public function init():void
        {
			//trace("Circle on init");
			radius = 50;
			spd = 8;
			//radius = 50;
			thickness = 2;
			//spd = 0.4;
            limit = 0;
            speed = 0;
            deg2rad = Math.PI/180;
            
			start = Math.random() * -100 + 50;
			end = Math.random() * -20 + 40;
            if(circle){removeChild(circle);}
            circle = new Sprite();
            circle.graphics.lineStyle(thickness, 0x2031EE, 1, false, "normal", "none");
			circle.alpha = 0.8;
            circle.graphics.moveTo(0, -radius);
            addChild(circle);
			
			var px:Number =  Math.sin((0 - start) * deg2rad) * radius + Math.random()*1 - 0.5 ;
			var py:Number = -Math.cos((0 - start) * deg2rad) * radius + Math.random()*1 - 0.5 - 3;
			circle.graphics.moveTo(px, py);
        }
        
		public function clear():void {
			graphics.clear();
			if(circle){
				circle.graphics.clear();
			}
		}
			
        public function draw_circle(e:Event):void
        {
			
            if(limit < 360 + end)
				{
				//spd = 0.4;
				//trace("drawing" + speed + spd);
				//speed += spd;
				speed += spd;
				var previousLimit:Number = limit;
				limit = speed*(360/100);
				
				//circle.graphics.clear();
				//circle.graphics.moveTo(0, -radius);
				radius -= Math.random()*0.6;
				
				for(var i:Number = previousLimit; i <= limit; i++)
				{
					var px:Number =  Math.sin((i - start) * deg2rad) * radius + Math.random()*1 - 0.5 ;
					var py:Number = -Math.cos((i - start) * deg2rad) * radius + Math.random()*1 - 0.5 - 3;
					circle.graphics.lineTo(px, py);
				}
				
					//limit = 360;
					//init(0 , 0);
            }
            
        }
        
    }
}