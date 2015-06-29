/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular.module('scFilters',[]).filter('postype',function(){
   return function(input){
      if(input === 'JOKE'){
          return '#/home/jokes?id=';
      }else if(input === 'PROVERB'){
          return '#/home/proverbs?id=';
      }else if(input === 'QUOTE'){
          return '#/home/quotes?id=';
      } 
   } ;
}).filter('searchurl',function(){
   return function(group,id){
      if(group === 'Persons'){
          return '#/profile/'+id;
      }
      else if(group === 'Jokes'){
          return '#/home/jokes?id='+id;
      }
      else if(group === 'Proverbs'){
          return '#/home/proverbs?id='+id;
      }
      else if(group === 'Quotes'){
          return '#/home/quotes?id='+id;
      }
   } ;
});
