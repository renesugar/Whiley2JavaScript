function test(){//method() -> void
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            var r2 = new WyJS.Integer(0);
            r1 = r2;//assign %1 = %2  : int
            r0 = r1;//assign %0 = %1  : int
            while(true){//loop (%0, %3, %4, %5, %6)
               var r3 = new WyJS.Integer(5);
               if(WyJS.gt(r0, r3, true)){
                  control_flow_pc = 560;
                  control_flow_repeat = true;
                  continue outer;
               }
               var r4 = new WyJS.Integer(3);
               if(WyJS.equals(r0, r4, false)){
                  var r5 = new WyJS.Integer(1);
                  var r6 = r0.add(r5);//add %6 = %0, %5 : int
                  r0 = r6;//assign %0 = %6  : int
                  continue;
               }
               control_flow_pc = 560;
               control_flow_repeat = true;
               continue outer;//goto label560
            }
         case 560:
            var r7 = new WyJS.Integer(3);
            if(WyJS.equals(r0, r7, true)){
               control_flow_pc = 562;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 562:
      }
   }
}
