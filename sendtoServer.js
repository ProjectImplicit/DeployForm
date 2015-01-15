 
 function sendToServer(xml,path,name,url,msg2,overwrite){
        
        
        var data={};
        //var path = this.params.folder;
        
        data.path='/user/'+path;
        data.FileName =name;
        data.submit='false';
        data.realPath = '';
        console.log('name: '+data.FileName+ ', folder: '+data.path);
        data.xml = xml;
        data.overwrite = overwrite;
        console.log(xml);
        $.ajax({
              type: 'POST',
              url: url,
              data: JSON.stringify(data),
              success: function(result) {
              		  var res = takespaces(result);	
                      //var res = result.length;
                      //res++;
                      if(res === '1'){
                        //alert('File was saved successfully.');
                        msg2.success=true;
                        msg2.text = "File was saved successfully, on: "+path+'/'+name;
                      }else{
                      	if (res=== '21'){
                      		msg2.success=false;
                      		msg2.text = "File was not saved, a file with this name already exist on the server.";
                      	}else{
                      		msg2.success =false;
                      		//alert('File was not saved on our servers, check your study folder name.');
                        	msg2.text = 'Study Folder was not Found, check your study folder name.';


                      	}
                        
                      }
                          
                  },
              fail: function(jqXHR, textStatus, errorThrown){
                  console.log(jqXHR);
                  console.log(textStatus);
                  console.log(errorThrown);

                  alert('fail');

              },
              dataType: 'text',
              async:false
        });
 	
 }
     