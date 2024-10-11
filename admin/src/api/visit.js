// import axios from 'axios'
import request from '@/utils/rest'
// import { getToken } from '@/utils/auth'

// const customRest = axios.create({
//   baseURL: process.env.VUE_APP_BASE_URL,
//   timeout: 5000 // request timeout
// })

export function getVisit(visitID) {
  return request({
    url: 'visit/' + visitID.toString(),
    method: 'get'
  })
}


export function getListVisit() {
  return request({
    url: 'visit',
    method: 'get'
  })
}

export function getCurrentAppointment() {
  return request({
    url: 'currentappointment',
    method: 'get'
  })
}

export function getOngoingVisit() {
  return request({
    url: 'ongoingvisit',
    method: 'get'
  })
}

//multipart form data
// export function postVisit(data, img1, img2) {
//   const formData = new FormData();
//   formData.append('img1', img1)
//   formData.append('img2', img2)
//   formData.append('data', JSON.stringify(data));
//   for (var pair of formData.entries()) {
//     console.log(pair[0] + ', ' + pair[1]);
//   }
//
//   return request({
//     url: 'visit',
//     method: 'post',
//     data: formData,
//     timeout: 12000
//   })
// }


export function postVisit(_data, img1, img2) {
  const alldata = {
    data: _data,
    img1: img1,
    img2: img2
  }

  return request({
    url: 'visit',
    method: 'post',
    data: alldata
  })
}


export function getVisitImage(id) {
  return request({
    url: 'visitimage/' + id.toString(),
    method: 'get'
  })
}

export function getVisitImgURL(id) {
  if (!id) return null;
  return process.env.VUE_APP_BASE_URL + '/image/' + id.toString();
}

export function endVisit(id) {
  return request({
    url: 'endvisit/' + id.toString(),
    method: 'get'
  })
}

export function searchVisit(dt1, dt2, filtertxt) {
  let _url = 'searchvisit/' + formatDate(dt1) + '/' + formatDate(dt2);
  if (filtertxt && filtertxt != '') _url = _url  + '/' + filtertxt;

  return request({
    url: _url,
    method: 'get'
  })
}

export function searchDocument(dt1, dt2, filtertxt) {
  let _url = 'searchdocument/' + formatDate(dt1) + '/' + formatDate(dt2);
  if (filtertxt && filtertxt != '') _url = _url  + '/' + filtertxt;

  return request({
    url: _url,
    method: 'get'
  })
}

export function getElapsedTime(startTime) {
  // Record end time
  const endTime = new Date();

  //server time over 2 minutes : vosp.tigaserangkai.co.id
  endTime.setMinutes(endTime.getMinutes() + 2);

  // Compute time difference in milliseconds
  let timeDiff = endTime.getTime() - startTime.getTime();

  // Convert time difference from milliseconds to seconds
  timeDiff = timeDiff / 1000;

  // Extract integer seconds that dont form a minute using %
  const seconds = Math.floor(timeDiff % 60); // ignoring uncomplete seconds (floor)

  // Pad seconds with a zero if neccessary
  const secondsAsString = seconds < 10 ? '0' + seconds : seconds + '';

  // Convert time difference from seconds to minutes using %
  timeDiff = Math.floor(timeDiff / 60);

  // Extract integer minutes that don't form an hour using %
  const minutes = timeDiff % 60; // no need to floor possible incomplete minutes, becase they've been handled as seconds

  // Pad minutes with a zero if neccessary
  const minutesAsString = minutes < 10 ? '0' + minutes : minutes + '';

  // Convert time difference from minutes to hours
  timeDiff = Math.floor(timeDiff / 60);

  // Extract integer hours that don't form a day using %
  const hours = timeDiff % 24; // no need to floor possible incomplete hours, becase they've been handled as seconds

  // Convert time difference from hours to days
  timeDiff = Math.floor(timeDiff / 24);

  // The rest of timeDiff is number of days
  const days = timeDiff;

  const totalHours = hours + (days * 24); // add days to hours
  const totalHoursAsString = totalHours < 10 ? '0' + totalHours : totalHours + '';

  if (totalHoursAsString === '00') {
    return minutesAsString + ':' + secondsAsString;
  } else {
    // return startTime; // 'Hours : ' + totalHoursAsString + ': Minutes : ' + minutesAsString + ': Seconds :' + secondsAsString;
    return  totalHoursAsString + ':' + minutesAsString + ':' + secondsAsString;
  }
}

function formatDate(date) {
  var d = new Date(date),
    month = '' + (d.getMonth() + 1),
    day = '' + d.getDate(),
    year = d.getFullYear();

  if (month.length < 2)
    month = '0' + month;
  if (day.length < 2)
    day = '0' + day;

  return [year, month, day].join('-');
}
