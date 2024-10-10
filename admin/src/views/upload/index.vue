<template>
  <div id="container" class="app-container">
    <el-form ref="form" :model="dialogData" label-width="120px" label-position="top">
      <el-form-item label="">
        <el-col :span="3">
          <el-select v-model="param_period" placeholder="Select Year" style="width:100%">
            <el-option
              v-for="period in periods"
              :key="period.id"
              :label="period.caption"
              :value="period.id"
            />
          </el-select>
        </el-col>
        <el-col :span="21">
          <el-select v-model="param_department_id" placeholder="Select Department" style="width:100%; margin-left:10px">
            <el-option
              v-for="dept in depts"
              :key="dept.id"
              :label="dept.deptname"
              :value="dept.id"
            >
              <span style="float: left">{{ dept.deptname }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ dept.deptcode }}</span>
            </el-option>
          </el-select>
        </el-col>
      </el-form-item>
    </el-form>

    <el-tabs type="border-card">
      <el-tab-pane>
        <span slot="label"><i class="el-icon-s-opportunity" />Maturity Level</span>
        <el-table
          v-loading="listLoading"
          :data="kpidept.mlitems"
          :cell-style="cellStyle"
          style="width: 100%"
          :span-method="onSpanMethod"
        >
          <el-table-column width="150" header-align="center" prop="areaname" label="Area" />
          <el-table-column label="Sub Area" header-align="center">
            <el-table-column width="70" header-align="center" prop="subcode" label="No" />
            <el-table-column width="250" header-align="center" prop="subdesc" label="Sub Name" />
          </el-table-column>
          <el-table-column width="180" header-align="center" prop="levelcode" label="Level">
            <template slot-scope="sc">
              <el-tag :type="getTagType(sc)"> Lv {{ sc.row.level }}</el-tag>
              {{ sc.row.levelcode }}
            </template>
          </el-table-column>
          <el-table-column header-align="center" prop="leveldetail" label="Uraian" />
          <el-table-column width="70" header-align="center" prop="weight" label="Bobot">
            <template slot-scope="sc">
              <el-tag :type="getTagType(sc)" effect="plain"> {{ sc.row.weight }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column width="110" label="Operations" header-align="center">
            <template slot-scope="sc">
              <el-button-group>
                <el-button type="success" size="small" icon="el-icon-upload2" @click.native.prevent="showDialog(sc.$index, kpidept.mlitems, false)" />
                <el-button type="primary" size="small" icon="el-icon-folder-opened" @click.native.prevent="showOpenDlg(sc.$index, kpidept.mlitems, false)" />
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane>
        <span slot="label"><i class="el-icon-s-opportunity" />KPI</span>
        <el-table
          v-loading="listLoading"
          :data="kpidept.kpiitems"
          :cell-style="cellStyle"
          style="width: 100%"
          :span-method="onSpanMethodKPI"
        >
          <el-table-column width="150" header-align="center" prop="areaname" label="Area" />
          <el-table-column label="Sub Area" header-align="center">
            <el-table-column width="70" header-align="center" prop="subcode" label="No" />
            <el-table-column width="250" header-align="center" prop="subdesc" label="Sub Name" />
          </el-table-column>
          <el-table-column width="180" header-align="center" prop="levelcode" label="Level">
            <template slot-scope="sc">
              <el-tag :type="getTagType(sc)"> Lv {{ sc.row.level }}</el-tag>
              {{ sc.row.levelcode }}
            </template>
          </el-table-column>
          <el-table-column header-align="center" prop="leveldetail" label="Uraian" />
          <el-table-column width="70" header-align="center" prop="weight" label="Bobot">
            <template slot-scope="sc">
              <el-tag :type="getTagType(sc)" effect="plain"> {{ sc.row.weight }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column width="110" label="Operations" header-align="center">
            <template slot-scope="sc">
              <el-button-group>
                <el-button type="success" size="small" icon="el-icon-upload2" @click.native.prevent="showDialog(sc.$index, kpidept.kpiitems, true)" />
                <el-button type="primary" size="small" icon="el-icon-folder-opened" @click.native.prevent="showOpenDlg(sc.$index, kpidept.kpiitems, true)" />
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog :title="dialogData.caption" :visible.sync="dialogVisible" label-position="top">
      <el-upload
        ref="upload"
        class="upload-demo"
        :action="getRestUploadURL()"
        :on-error="handleUploadError"
        :on-success="handleUploadSuccess"
        multiple
        :file-list="fileList"
        :auto-upload="false"
      >
        <el-button slot="trigger" size="small" type="primary">select file</el-button>
        <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">upload to server</el-button>
      </el-upload>

      <br>
      <p>Uploaded File : </p>
      <el-table :data="dbFileList">
        <el-table-column width="500" header-align="center" prop="filename" label="FileName" />
        <el-table-column width="100" label="Operations" header-align="center">
          <template slot-scope="sc">
            <el-button type="danger" size="small" @click.native.prevent="deleteUploadFile(sc.row.id)">
              <i class="el-icon-delete" />  Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog :title="dialogFile.caption" :visible.sync="dialogFileVisible" label-position="top" width="90%">
      <div v-for="(item, index) in dbFileList" :key="item.id">
        <el-table :data="getFileData(index)" border style="width: 100%" :show-header="false" :cell-style="cellStyleFile">
          <el-table-column prop="caption" label="" width="120" />
          <el-table-column label="">
            <template slot-scope="sc">
              <div v-if="(!sc.row.islink)&&(!sc.row.isimg)">{{ sc.row.value }}</div>
              <el-image v-if="sc.row.isimg" :src="sc.row.value" />
              <el-link v-if="sc.row.islink" :href="sc.row.value" type="primary" style="margin-bottom: 10px">Download File</el-link>
            </template>
          </el-table-column>
        </el-table>
        <br>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogFileVisible = false">Close</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getListDept } from '@/api/product'
import { spanRow } from '@/utils/spanRow'
import { getUploadURLML, getUploadURLKPI, getFileListML, getFileListKPI, downloadFile, deleteFile, getPeriod, genKPIDept } from '@/api/kpidept'
import { Message } from 'element-ui'
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      listLoading: null,
      param_period: null,
      param_department_id: null,
      param_iskpi: null,
      param_subcode: null,
      param_level: null,
      kpidept: {
        mlitems: [],
        kpiitems: []
      },
      depts: [],
      periods: [],
      optionSpan: [
        { index: 0, field: 'areaname' },
        { index: 1, field: 'subcode' },
        { index: 2, field: 'subdesc' }
      ],
      dialogData: {},
      dialogVisible: false,
      dialogOpenVisible: false,
      fileList: [],
      dbFileList: [],

      dialogFile: {},
      dialogFileVisible: false
      // fileData: [],
    }
  },
  computed: {
    ...mapGetters([
      'name', 'department_id'
    ])
  },
  watch: {
    param_period(val) {
      this.fetchData();
    },
    param_department_id(val) {
      this.fetchData();
    }
  },
  created() {
    this.initForm();
  },
  methods: {
    initForm() {
      getListDept().then(response => {
        this.depts = response.data;
        if (this.department_id > 0) {
          for (var i = this.depts.length - 1; i >= 0; i--) {
            if (this.depts[i].id !== this.department_id) {
              this.depts.splice(i, 1);
            }
          }
        }
      });
      getPeriod().then(response => {
        this.periods = response.data;
      })

      var today = new Date();
      var smst = Math.floor((today.getMonth() + 5) / 6);
      this.param_period = today.getFullYear().toString() + ('0' + smst.toString()).substring(0, 2);
    },
    fetchData() {
      this.listLoading = true
      var deptid = 0;
      var period = 0;
      if (this.param_department_id) deptid = this.param_department_id;
      if (this.param_period) period = this.param_period;

      if (period === 0 || deptid === 0) {
        this.listLoading = false;
        return;
      }

      genKPIDept(deptid, period).then(response => {
        this.kpidept = response.data;

        var lvl = '';
        var indexLvl = 0;
        for (var item of this.kpidept.mlitems) {
          if (item.subcode === lvl) {
            item.indexLvl = indexLvl
          } else {
            lvl = item.subcode;
            indexLvl++;
            item.indexLvl = indexLvl;
          }
        }

        indexLvl = 0;
        for (item of this.kpidept.kpiitems) {
          if (item.subcode === lvl) {
            item.indexLvl = indexLvl
          } else {
            lvl = item.subcode;
            indexLvl++;
            item.indexLvl = indexLvl;
          }
        }
        this.listLoading = false;
      })
    },
    onSpanMethod({ row, column, rowIndex, columnIndex }) {
      return spanRow({ row, column, rowIndex, columnIndex }, this.kpidept.mlitems, this.optionSpan)
    },
    onSpanMethodKPI({ row, column, rowIndex, columnIndex }) {
      return spanRow({ row, column, rowIndex, columnIndex }, this.kpidept.kpiitems, this.optionSpan)
    },
    showDialog(index, items, iskpi) {
      this.dialogData.caption = 'Upload Evident : ' + items[index].subname;
      this.param_subcode = items[index].subcode;
      this.param_level = items[index].level;
      this.param_iskpi = iskpi;

      // console.log(this.param_iskpi);
      if (!this.param_period) return;
      if (!this.param_department_id) return;
      if (!this.param_subcode) return;
      if (!this.param_level) return;
      // if (!this.param_iskpi) return;
      this.getFileList();
      this.dialogVisible = true;
    },
    showOpenDlg(index, items, iskpi) {
      this.dialogFile.caption = 'Browse Evident : ' + items[index].subname;
      this.param_subcode = items[index].subcode;
      this.param_level = items[index].level;
      this.param_iskpi = iskpi;

      if (!this.param_period) return;
      if (!this.param_department_id) return;
      if (!this.param_subcode) return;
      if (!this.param_level) return;
      this.getFileList();
      this.dialogFileVisible = true;
    },
    getFileList() {
      if (this.param_iskpi) {
        getFileListKPI(this.param_period, this.param_department_id, this.param_subcode, this.param_level).then(response => {
          this.dbFileList = response.data;
        }).catch(() => {
          this.dbFileList = [];
        })
      } else {
        getFileListML(this.param_period, this.param_department_id, this.param_subcode, this.param_level).then(response => {
          this.dbFileList = response.data;
        }).catch(() => {
          this.dbFileList = [];
        })
      }
    },
    getFileData(index) {
      var item = this.dbFileList[index];
      var ar = [];
      ar.push({ caption: 'File Name', value: item.filename, link: process.env.VUE_APP_BASE_URL + '/downloadfile/' + item.id.toString() });
      ar.push({ caption: 'User Upload', value: item.username });
      ar.push({ caption: 'Upload Date', value: item.upload_date });

      if (item.filename.match(/.(jpg|jpeg|png|gif)$/i)) {
        ar.push({ caption: '', value: (
          process.env.VUE_APP_BASE_URL + '/downloadfile/' + item.id.toString()
        ), isimg: true, islink: true });
      } else {
        ar.push({ caption: '', value: (
          process.env.VUE_APP_BASE_URL + '/downloadfile/' + item.id.toString()
        ), islink: true });
      }

      return ar;
    },
    download(id) {
      downloadFile(id);
    },
    deleteUploadFile(id) {
      var vm = this;
      this.$confirm('Anda yakin menghapus File ini?', 'Warning', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        deleteFile(id).then(response => {
          vm.getFileList();
        })
      })
    },
    cellStyle({ row, column, rowIndex, columnIndex }) {
      var str = '';

      if ((row.indexLvl % 2) === 0) {
        str = str + ' background-color: rgb(255, 255, 228); '
      }

      if ((columnIndex === 0) || (row.level === 1 && columnIndex === 2) || (columnIndex === 1)) {
        str = str + ' font-weight: bold; color: rgb(64, 158, 255);'
      }

      if ((row.level > 1 && columnIndex === 2) || (columnIndex === 3)) {
        str = str + ' font-size: 13px; line-height:1;';
      }
      return str;
    },
    submitUpload() {
      this.$refs.upload.submit();
    },
    getRestUploadURL() {
      var url = '';
      if (this.param_iskpi) {
        url = getUploadURLKPI(this.param_period, this.param_department_id, this.param_subcode, this.param_level);
      } else {
        url = getUploadURLML(this.param_period, this.param_department_id, this.param_subcode, this.param_level);
      }
      return url;
    },
    handleUploadError(err, file, fileList) {
      console.log(err);
      Message({
        message: 'Error Upload File, Cek console untuk detail',
        type: 'error',
        duration: 5 * 1000
      })
    },
    handleUploadSuccess(response, file, fileList) {
      // console.log('sucess');
      this.getFileList();
      // this.dialogVisible = false;
      this.$message({
        type: 'success',
        message: response
      });
    },
    getTagType(sc) {
      if (sc.row.level === 1) return 'danger';
      if (sc.row.level === 2) return 'warning';
      if (sc.row.level === 3) return 'info';
      if (sc.row.level === 4) return '';
      if (sc.row.level === 5) return 'success';
    },
    cellStyleFile({ row, column, rowIndex, columnIndex }) {
      var str = '';
      if (columnIndex === 1 && rowIndex === 0) {
        str = str + ' font-weight: bold;'
      }
      if (columnIndex === 0) {
        str = str + ' background-color: #304156; color: rgb(191, 203, 217);'
      }

      return str;
    }

  }
}
</script>

<style scoped>
  .el-table{
    /* table-layout: fixed; */
    /* overflow-wrap: break-word;
    word-break: break-word; */
  }
  .el-table >>> .cell {
    word-break: break-word;
    white-space: pre-wrap;
    /* word-break: normal;
    line-height: 15px;
    font-size: 12px; */
    /* white-space: pre; */
    /* text-overflow: clip; */
    /* overflow-wrap: break-word; */
  }

  .el-table >>> thead {
    color: rgb(191, 203, 217);;
    font-weight: 500;
    /* background: #304156;   */
  }

  .el-table >>> thead.is-group th {
    background: #304156;
  }
</style>
