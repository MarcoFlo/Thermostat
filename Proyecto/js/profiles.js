window.onload = function(){
	var minus_wake =document.getElementById("minus_wake").onclick = select;
	var plus_wake =document.getElementById("plus_wake").onclick = select;
	var minus_leave =document.getElementById("minus_leave").onclick = select;
	var plus_leave =document.getElementById("plus_leave").onclick = select;
	var minus_return =document.getElementById("minus_return").onclick = select;
	var plus_return =document.getElementById("plus_return").onclick = select;
	var minus_sleep =document.getElementById("minus_sleep").onclick = select;
	var plus_sleep =document.getElementById("plus_sleep").onclick = select;
	var minus_wakef =document.getElementById("minus_wakef").onclick = select;
	var plus_wakef =document.getElementById("plus_wakef").onclick = select;
	var minus_leavef =document.getElementById("minus_leavef").onclick = select;
	var plus_leavef =document.getElementById("plus_leavef").onclick = select;
	var minus_returnf =document.getElementById("minus_returnf").onclick = select;
	var plus_returnf =document.getElementById("plus_returnf").onclick = select;
	var minus_sleepf =document.getElementById("minus_sleepf").onclick = select;
	var plus_sleepf =document.getElementById("plus_sleepf").onclick = select;
	
	var minus_temp_wake =document.getElementById("minus_temp_wake").onclick = select;
	var plus_temp_wake =document.getElementById("plus_temp_wake").onclick = select;
	var minus_temp_leave =document.getElementById("minus_temp_leave").onclick = select;
	var plus_temp_leave =document.getElementById("plus_temp_leave").onclick = select;
	var minus_temp_return =document.getElementById("minus_temp_return").onclick = select;
	var plus_temp_return =document.getElementById("plus_temp_return").onclick = select;
	var minus_temp_sleep =document.getElementById("minus_temp_sleep").onclick = select;
	var plus_temp_sleep =document.getElementById("plus_temp_sleep").onclick = select;
	var minus_temp_wakef =document.getElementById("minus_temp_wakef").onclick = select;
	var plus_temp_wakef =document.getElementById("plus_temp_wakef").onclick = select;
	var minus_temp_leavef =document.getElementById("minus_temp_leavef").onclick = select;
	var plus_temp_leavef =document.getElementById("plus_temp_leavef").onclick = select;
	var minus_temp_returnf =document.getElementById("minus_temp_returnf").onclick = select;
	var plus_temp_returnf =document.getElementById("plus_temp_returnf").onclick = select;
	var minus_temp_sleepf =document.getElementById("minus_temp_sleepf").onclick = select;
	var plus_temp_sleepf =document.getElementById("plus_temp_sleepf").onclick = select;
	
	//alert(numero);
}
function select(){
	var name= new String(this.id);
	var names = name.split("_");
	//alert(names[1]);
	//alert(name[name.length - 1]);
	if(name[name.length - 1] != "f" && names.length==2){
		var aux=document.getElementsByName(String(names[1])+ "_1")[0].value;
		var num = parseFloat(aux);
		if(names[0] == "minus")
			num--;
		else
			num++;
		if(num<=0)
			num=0;
		else if(num>=23)
			num=23;
		document.getElementsByName(String(names[1])+ "_1")[0].value = num + ":00";
		//alert(aux);
	}else if(name[name.length - 1] == "f" && names.length==2){
		names[1] = names[1].substring(0,names[1].length-1);
		var aux=document.getElementsByName(String("f" +names[1])+ "_1")[0].value;
		var num = parseFloat(aux);
		if(names[0] == "minus")
			num--;
		else
			num++;
		if(num<=0)
			num=0;
		else if(num>=23)
			num=23;
		document.getElementsByName(String("f"+names[1])+ "_1")[0].value = num + ":00";
	}else if(names[1] == "temp"){
		if(name[name.length - 1] == "f"){
			names[2] = names[2].substring(0,names[2].length-1);
			var aux=document.getElementsByName(String("f"+names[1])+ "_"+names[2])[0].value;
			var num = parseFloat(aux);
			if(names[0] == "minus")
				num--;
			else
				num++;
			if(num<=0)
				num=0;
			else if(num>=30)
				num=30;
			document.getElementsByName(String("f"+names[1])+ "_"+names[2])[0].value = num;
		}else{
			var aux=document.getElementsByName(String(names[1])+ "_"+names[2])[0].value;
			var num = parseFloat(aux);
			if(names[0] == "minus")
				num--;
			else
				num++;
			if(num<=0)
				num=0;
			else if(num>=30)
				num=30;
			document.getElementsByName(String(names[1])+ "_"+names[2])[0].value = num;
		}
	}	
}

//wake_1.value = sleep2 + 1;
