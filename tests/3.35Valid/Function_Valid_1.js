function f(r0){//function(real) -> int
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            var r1 = new WyJS.Integer(1);
            return r1;//return %1 : int
      }
   }
}

function f(r0){//function(int) -> int
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            var r1 = new WyJS.Integer(2);
            return r1;//return %1 : int
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
            var r0 = f(r1);//invoke %0 = (%1) Function_Valid_1:f : function(int) -> int
            var r2 = new WyJS.Integer(2);
            if(WyJS.equals(r0, r2, true)){
               control_flow_pc = 1800;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1800:
            var r4 = new WyJS.Real(1.23);
            var r3 = f(r4);//invoke %3 = (%4) Function_Valid_1:f : function(real) -> int
            var r5 = new WyJS.Integer(1);
            if(WyJS.equals(r3, r5, true)){
               control_flow_pc = 1801;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1801:
      }
   }
}
