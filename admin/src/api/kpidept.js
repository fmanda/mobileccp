import request from '@/utils/rest';

export function getUploadURLKPI(yearperiod, deptid, subcode, level) {
  if (!yearperiod) return '';
  if (!deptid) return '';
  if (!subcode) return '';
  if (!level) return '';
  return process.env.VUE_APP_BASE_URL + '/kpidept_upload_kpi/' +
    yearperiod.toString() + '/' +
    deptid + '/' + subcode + '/' + level.toString();
}

export function getUploadURLML(yearperiod, deptid, subcode, level) {
  if (!yearperiod) return '';
  if (!deptid) return '';
  if (!subcode) return '';
  if (!level) return '';
  return process.env.VUE_APP_BASE_URL + '/kpidept_upload_ml/' +
    yearperiod.toString() + '/' +
    deptid + '/' + subcode + '/' + level.toString();
}

export function getUploadLog() {
  return request({
    url: 'uploadlog',
    method: 'get'
  })
}

export function getPeriod() {
  return request({
    url: 'period',
    method: 'get'
  })
}

export function getFileListML(yearperiod, deptid, subcode, level) {
  return request({
    url: 'filelistml/' + yearperiod.toString() + '/' +
      deptid + '/' + subcode + '/' + level.toString(),
    method: 'get'
  })
}

export function getFileListKPI(yearperiod, deptid, subcode, level) {
  return request({
    url: 'filelistml/' + yearperiod.toString() + '/' +
      deptid + '/' + subcode + '/' + level.toString(),
    method: 'get'
  })
}

export function deleteFile(id) {
  return request({
    url: 'deleteFile/' + id.toString(),
    method: 'get'
  })
}

export function downloadFile(id) {
  var url = process.env.VUE_APP_BASE_URL + '/downloadfile/' + id.toString();
  window.open(url, '_blank');
}

export function genKPIDept(deptid, period) {
  return request({
    url: 'genkpidept/' + deptid.toString() + '/' + period.toString(),
    method: 'get'
  })
}

export function getKPIDept(deptid, period) {
  return request({
    url: 'kpidept/' + deptid.toString() + '/' + period.toString(),
    method: 'get'
  })
}

export function getKPIDeptPrev(deptid, period) {
  return request({
    url: 'kpideptprev/' + deptid.toString() + '/' + period.toString(),
    method: 'get'
  })
}

export function postKPIDept(data) {
  return request({
    url: 'kpidept',
    method: 'post',
    data
  })
}
