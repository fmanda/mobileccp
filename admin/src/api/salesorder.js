// import axios from 'axios'
import request from '@/utils/rest'
import { getProjectCode } from '@/utils/auth'


export function getSalesOrderPeriod(dt1, dt2, filtertxt) {
  let _url = 'salesorderbyperiod/' + getProjectCode()  + '/'
            + formatDate(dt1) + '/' + formatDate(dt2);
  if (filtertxt && filtertxt != '') _url = _url  + '/' + filtertxt;

  return request({
    url: _url,
    method: 'get'
  })
}


export function getVisitPeriod(dt1, dt2, filtertxt) {
  let _url = 'newccp/' + getProjectCode()  + '/'
            + formatDate(dt1) + '/' + formatDate(dt2);
  if (filtertxt && filtertxt != '') _url = _url  + '/' + filtertxt;

  return request({
    url: _url,
    method: 'get'
  })
}

function formatDate(date) {
  var d = new Date(date);


  var dformat = [d.getFullYear(),
    (d.getMonth()+1).padLeft(),
    d.getDate().padLeft(),
    ].join('-') ;

  // var dformat = [d.getFullYear(),
  //               (d.getMonth()+1).padLeft(),
  //               d.getDate().padLeft(),
  //               ].join('-') +' '
  //               +
  //               [d.getHours().padLeft(),
  //               d.getMinutes().padLeft(),
  //               d.getSeconds().padLeft()
  //               ].join(':');

  return dformat;
}


Number.prototype.padLeft = function(base,chr){
   var  len = (String(base || 10).length - String(this).length)+1;
   return len > 0? new Array(len).join(chr || '0')+this : this;
}
