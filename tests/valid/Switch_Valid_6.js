function f$Y9bFXA$W(r0){//function(int) -> int
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            if(WyJS.equals(r0, new WyJS.Integer(1), true)){
               control_flow_pc = 1127;
               control_flow_repeat = true;
               continue outer;
            }
             else if(WyJS.equals(r0, new WyJS.Integer(2), true)){
               control_flow_pc = 1128;
               control_flow_repeat = true;
               continue outer;
            }
            else{
               control_flow_pc = 1126;
               control_flow_repeat = true;
               continue outer;
            }
         case 1127:
            var r1 = new WyJS.Integer(0);
            return r1;//return %1 : int
            control_flow_pc = 1126;
            control_flow_repeat = true;
            continue outer;//goto label1126
         case 1128:
            var r2 = new WyJS.Integer(1);
            var r3 = r2.neg();//neg %3 = %2 : int
            return r3;//return %3 : int
            control_flow_pc = 1126;
            control_flow_repeat = true;
            continue outer;//goto label1126
         case 1126:
            var r4 = new WyJS.Integer(10);
            return r4;//return %4 : int
            return;
      }
   }
}

function test(){//method() -> void
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            var r1 = new WyJS.Integer(1);
            var r0 = f$Y9bFXA$W(r1);//invoke %0 = (%1) Switch_Valid_6:f : function(int) -> int
            var r2 = new WyJS.Integer(0);
            if(WyJS.equals(r0, r2, true)){
               control_flow_pc = 1129;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1129:
            var r4 = new WyJS.Integer(2);
            var r3 = f$Y9bFXA$W(r4);//invoke %3 = (%4) Switch_Valid_6:f : function(int) -> int
            var r5 = new WyJS.Integer(1);
            var r6 = r5.neg();//neg %6 = %5 : int
            if(WyJS.equals(r3, r6, true)){
               control_flow_pc = 1130;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1130:
            var r8 = new WyJS.Integer(3);
            var r7 = f$Y9bFXA$W(r8);//invoke %7 = (%8) Switch_Valid_6:f : function(int) -> int
            var r9 = new WyJS.Integer(10);
            if(WyJS.equals(r7, r9, true)){
               control_flow_pc = 1131;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1131:
            var r11 = new WyJS.Integer(1);
            var r12 = r11.neg();//neg %12 = %11 : int
            var r10 = f$Y9bFXA$W(r12);//invoke %10 = (%12) Switch_Valid_6:f : function(int) -> int
            var r13 = new WyJS.Integer(10);
            if(WyJS.equals(r10, r13, true)){
               control_flow_pc = 1132;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1132:
            return;
      }
   }
}

