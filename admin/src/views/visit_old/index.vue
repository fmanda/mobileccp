<template>
  <div class="app-container">
    <el-alert v-if="errormsg != '' "
      :title="errormsg"
      type="error"
      show-icon>
    </el-alert>

    <el-menu
      :default-active="activeMenu"
      class="el-menu-demo"
      mode="horizontal"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      @select="handleSelect"
      style = "margin-bottom: 10px"
    >
      <el-menu-item index="1"><i class="el-icon-user"></i>Sedang Berlangsung</el-menu-item>
      <el-menu-item index="2"><i class="el-icon-alarm-clock"></i>Janji Minggu Ini</el-menu-item>
      <el-menu-item index="3"><i class="el-icon-date"></i>Semua Kunjungan</el-menu-item>
      <el-menu-item index="4"><i class="el-icon-document"></i>Dokumen Masuk</el-menu-item>
    </el-menu>

    <div v-if= "activeMenu >= '3' ">
      <el-date-picker
        v-model="filterperiod"
        type="daterange"
        align="right"
        unlink-panels
        range-separator="To"
        start-placeholder="Start date"
        end-placeholder="End date"
        :picker-options="pickerOptions"
        @change="handleChangePeriod"
        style = "float: right; margin-left: 10px; margin-bottom: 10px;"
        >
      </el-date-picker>
      <el-input
        placeholder="Pencarian"
        prefix-icon="el-icon-search"
        style = "width: 400px; float: right; margin-bottom: 10px;"
        @change="handleChangeInputSearch"
        clearable
        v-model="filtertxt">
      </el-input>
    </div>

    <el-table
      :v-loading="listLoading"
      :data="data"
      style="width: 100%"
    >
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-descriptions class="margin-top" style="margin-left: 50px;" title="" :column="1" size="" border>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-mobile-phone"></i>
                No. Telepon
              </template>
              {{props.row.phone}}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-location-outline"></i>
                Instansi
              </template>
              {{props.row.company}}
            </el-descriptions-item>
            <el-descriptions-item label-class-name="my-label">
              <template slot="label">
                <i class="el-icon-office-building"></i>
                Alamaat
              </template>
              {{props.row.address}}
            </el-descriptions-item>
            <el-descriptions-item label-class-name="my-label">
              <template slot="label">
                <i class="el-icon-tickets"></i>
                Keperluan
              </template>
              {{props.row.reason}}
            </el-descriptions-item>
          </el-descriptions>
        </template>
      </el-table-column>

      <el-table-column label="Pengunjung" prop="visitorname" sortable/>
      <el-table-column label="Menemui" prop="person_to_meet" sortable/>
      <el-table-column v-if = "activeMenu === '4'" label="Nama Dokumen" prop="documentname" sortable/>
      <el-table-column v-else label="Department" prop="deptname" sortable/>

      <el-table-column
        v-if = "activeMenu === '2'"
        label="Jadwal"
        width="180"
        sortable
        >
        <template slot-scope="scope">
          <span style="margin-left: 10px">{{ scope.row.entrydate.substring(11,19) }}</span>
        </template>
      </el-table-column>

      <el-table-column
        v-else
        label="Masuk"
        width="180"
        sortable
        >
        <template slot-scope="scope">
          <span style="margin-left: 10px">{{ scope.row.entrydate }}</span>
        </template>
      </el-table-column>

      <el-table-column
        v-if="activeMenu == '1'"
        label="Durasi"
        width="120"
        sortable
        >
        <template slot-scope="scope">
          <el-tag type="danger" effect="dark">
            <i class="el-icon-time"></i>
            <span style="margin-left: 10px">{{ scope.row.elapsed }}</span>
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        v-if="activeMenu == '3'"
        label="Keluar"
        width="180"
        sortable
        >
        <template slot-scope="scope">
          <!-- <i class="el-icon-time"></i> -->
          <span style="margin-left: 10px">{{ scope.row.exitdate }}</span>
        </template>
      </el-table-column>

      <el-table-column
        fixed="right"
        label="Status"
        width="140">
        <template slot-scope="scope">
          <el-button-group>
            <el-button v-if="['1','4'].includes(activeMenu)" plain icon="el-icon-edit-outline" size="small" @click="handleEdit(scope.$index, scope.row)"></el-button>
            <el-button v-if="activeMenu === '1'" plain size="small" @click="handleEndVisit(scope.row)" >Keluar</el-button>
            <el-button v-if="['3','4'].includes(activeMenu)" plain icon="el-icon-view" size="small" @click="handleEdit(scope.$index, scope.row, true)"></el-button>
            <el-button v-if="['2'].includes(activeMenu)" type="success"
              icon="el-icon-circle-check" size="small" @click="handleAppointment(scope.$index, scope.row)">Masuk</el-button>
          </el-button-group>
        </template>
      </el-table-column>

    </el-table>
    <br>
    <el-button v-if="['1','3','4'].includes(activeMenu)" type="primary" icon="el-icon-plus" @click.native.prevent="handleNew()">Data Kunjungan</el-button>
    <el-button v-if="activeMenu === '4'" type="primary" icon="el-icon-plus" @click.native.prevent="handleNew()">Dokumen Masuk</el-button>

    <el-dialog :title="dialogData.caption" :visible.sync="dialogVisible" width="900px" :before-close="handleCloseDlg"
      class="inputdialog" top="5vh"
    >
      <el-alert style="white-space: pre-line"
        description="Pihak PT Tiga Serangkai akan menjaga kerahasiaan data, identitas, dan dokumen yang diberikan,
        dan tidak akan menggunakannya selain untuk keperluan internal, dan tidak melanggar hukum yang berlaku"
        :closable="false"
        show-icon
        effect="dark"
        type="info">
      </el-alert>
      <br/>
      <el-row>
        <el-col :span="11" >
          <el-descriptions class="margin-top" title="" :column="1" border>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-user"></i>
                Nama
              </template>
              <!-- <el-input v-model="dialogData.visitor.visitorname"></el-input> -->
              <el-select
                v-model="selectedVisitorID"
                filterable
                remote
                reserve-keyword
                allow-create
                default-first-option
                placeholder="Input / Pilih Nama Pengunjung"
                :remote-method="searchVisitor"
                :loading="visitorLoading"
                style = "width:100%"
              >
                <el-option
                  v-for="visitor in visitors"
                  :key="visitor.id"
                  :label="visitor.visitorname"
                  :value="visitor.id">
                </el-option>

              </el-select>
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-office-building"></i>
                Instansi
              </template>
              <el-input v-model="dialogData.visitor.company"></el-input>
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-mobile-phone"></i>
                Telepon
              </template>
              <el-input v-model="dialogData.visitor.phone"></el-input>
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-location-outline"></i>
                Alamat
              </template>
              <el-input
                type="textarea"
                :rows="2"
                v-model="dialogData.visitor.address">
              </el-input>
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-mobile-phone"></i>
                Identitas
              </template>
              <el-input v-model="dialogData.visitor.idcardno"></el-input>
            </el-descriptions-item>
          </el-descriptions>
        </el-col>

        <el-col :span="13" style="padding-left:10px">
          <el-descriptions class="margin-top" title="" :column="1" border>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-user"></i>
                Department
              </template>
              <el-select v-model="param_department_id" placeholder="Select Department" style="width:85%">
                <el-option
                  v-for="dept in depts"
                  :key="dept.id"
                  :label="dept.deptname"
                  :value="dept.id"
                >
                  <span style="float: left">{{ dept.deptname }}</span>
                  <!-- <span style="float: right; color: #8492a6; font-size: 13px">{{ dept.deptcode }}</span> -->
                </el-option>
              </el-select>

              <!-- <el-input v-model="dialogData.department.deptname"></el-input> -->
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-office-building"></i>
                Karyawan
              </template>

              <el-select
                v-model="dialogData.person_to_meet"
                filterable
                remote
                reserve-keyword
                allow-create
                default-first-option
                placeholder="Input / Pilih Karyawan"
                :remote-method="searchEmployee"
                :loading="employeeLoading"
                style = "width:85%"
              >
                <el-option
                  v-for="employee in listemployee"
                  :key="employee.employeename"
                  :label="employee.employeename"
                  :value="employee.employeename">
                </el-option>

              </el-select>

              <!-- <el-input v-model="dialogData.person_to_meet"></el-input> -->
            </el-descriptions-item>

            <el-descriptions-item v-if="activeMenu != '4'">
              <template slot="label">
                <i class="el-icon-location-outline"></i>
                Keperluan
              </template>
              <el-input v-model="dialogData.reason">
              </el-input>
            </el-descriptions-item>

            <el-descriptions-item v-if="activeMenu === '4'">
              <template slot="label">
                <i class="el-icon-document"></i>
                Dokumen
              </template>
              <el-input v-model="dialogData.documentname">
              </el-input>
            </el-descriptions-item>


            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-mobile-phone"></i>
                Foto
              </template>
                <el-row>
                  <el-col :span="12" style = "padding-right:10px">
                    <el-skeleton style="width: 100%">
                      <template slot="template">
                        <el-image style="width: 100%; height: 131px;" :src="img1" >
                            <el-skeleton-item slot="error"  variant="image" style="width: 100%; height:100%" />
                        </el-image>
                        <!-- <el-button type="text" size="mini" style="width: 100%;" @click.native.prevent="showDialogPhoto()">Foto Pengunjung</el-button> -->
                      </template>

                    </el-skeleton>
                  </el-col>
                  <el-col :span="12" style = "padding-right:10px">
                    <el-skeleton style="width: 100%">
                      <template slot="template">
                        <el-image style="width: 100%; height: 131px;" :src="img2" >
                            <el-skeleton-item slot="error"  variant="image" style="width: 100%; height:100%" />
                        </el-image>
                      </template>
                    </el-skeleton>
                  </el-col>
                </el-row>

              </el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button style ="float: left;"  :disabled = "dialogReadOnly" @click.native.prevent="showDialogPhoto(1)">
          <span v-if="activeMenu === '4'">Foto Dokumen 1</span>
          <span v-else>Foto Pengunjung</span>
        </el-button>
        <el-button style ="float: left;" :disabled = "dialogReadOnly"  @click.native.prevent="showDialogPhoto(2)">
          <span v-if="activeMenu === '4'">Foto Dokumen 2</span>
          <span v-else>Foto Identitas</span>
        </el-button>
        <el-button type="primary" :disabled = "dialogReadOnly" @click.native.prevent="saveData()">Simpan</el-button>
        <el-button type="danger"  @click="dialogVisible = false">Batal</el-button>
      </span>
    </el-dialog>

    <el-dialog title="Foto Pengunjung" :visible.sync="dialogPhotoVisible" width="800px" height="300px"
      top="5vh" class="inputdialog"
    >
      <el-alert style="white-space: pre-line"
        description="Pihak PT Tiga Serangkai akan menjaga kerahasiaan data, identitas, dan dokumen yang diberikan,
        dan tidak akan menggunakannya selain untuk keperluan internal, dan tidak melanggar hukum yang berlaku"
        :closable="false"
        show-icon
        effect="dark"
        type="info">
      </el-alert>
      <code v-if="device">{{ device.label }}</code>
        <web-cam
          ref="webcam"
          :device-id="deviceId"
          width="100%"
          height="100%"
          @started="onStarted"
          @stopped="onStopped"
          @error="onError"
          @cameras="onCameras"
          @camera-change="onCameraChange" />
      <span slot="footer" class="dialog-footer">
        <el-select v-model="camera" style ="float: left;">
          <el-option value="null">-- Select Device --</el-option>
          <el-option
            v-for="device in devices"
            :key="device.deviceId"
            :label="device.label"
            :value="device.deviceId">
          </el-option>

        </el-select>
        <el-button type="primary" :disabled = "dialogReadOnly" style ="float: center;" @click.native.prevent="onCapture">Ambil Foto</el-button>
        <el-button type="danger" :disabled = "dialogReadOnly" @click="dialogPhotoVisible = false">Batal</el-button>
        <!-- <el-button type="primary" style ="right: center;" @click.native.prevent="onStart">Start Camera</el-button> -->
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getVisit, getListVisit, postVisit, getVisitImgURL, getElapsedTime,
        endVisit, getOngoingVisit, searchVisit, searchDocument, getCurrentAppointment } from '@/api/visit'
import { getAppointment } from '@/api/salesorder'
import { getListVisitor, getVisitor } from '@/api/visitor'
import { getListDept, getListEmployee } from '@/api/product'
import { WebCam } from 'vue-web-cam';
import { find, head } from 'lodash';

export default {
  components: {
    WebCam
  },
  data() {
    return {
      data: [],
      listLoading: true,
      search: '',
      errormsg: '',
      dialogData: {
        caption: '',
        visitor: {
        },
        department: {
        }
      },
      dialogVisible: false,
      // loadingDialog: false,
      dialogPhotoVisible: false,
      dialogPhotoActiveIdx: 0,
      activeMenu: '1',
      img1: null,
      img2: null,
      camera: null,
      deviceId: null,
      devices: [],
      param_department_id: null,
      depts: [],
      intervalEvents: [],
      visitors: [],
      visitorLoading: false,
      listemployee: [],
      employeeLoading: false,
      selectedVisitorID: null,
      filterperiod: [new Date() -(3600 * 1000 * 24 * 30), new Date()],
      pickerOptions: {
        shortcuts: [{
          text: 'Seminggu Terakhir',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: 'Sebulan Terakhir',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '3 Bulan Terakhir',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      filtertxt: '',
      dialogReadOnly: false
      // isdocument: false
    }
  },
  created() {
    // this.fetchData()
    // this.setTimers();
  },
  beforeDestroy() {
    this.intervalEvents.map(intervalEvent => {
      clearInterval(intervalEvent)
    })
  },
  computed: {
    device() {
      return find(this.devices, n => n.deviceId === this.deviceId);
    }
  },
  watch: {
    camera: function(id) {
      this.deviceId = id;
    },
    devices: function() {
      // Once we have a list select the first one
      const first = head(this.devices);
      if (first) {
        this.camera = first.deviceId;
        this.deviceId = first.deviceId;
      }
    },
    dialogPhotoVisible: function() {
      if (this.$refs.webcam) {
        if (this.dialogPhotoVisible) {
          this.$refs.webcam.start();
        } else {
          // console.log('stop');
          // this.$refs.webcam.stop();
        }
      }
    },
    selectedVisitorID: function() {
      if (!this.selectedVisitorID) {
        this.dialogData.visitor = {
          id: 0
        }
        this.dialogData.visitor_id = 0;
        return;
      }
      // console.log(this.dialogData.visitor_id);
      // console.log(this.selectedVisitorID);

      // not number
      if (isNaN(this.selectedVisitorID)) {
        this.dialogData.visitor = {
          id: 0,
          visitorname: this.selectedVisitorID
        }
        this.dialogData.visitor_id = 0;
        return;
      }

      // fetch api
      if (this.dialogData.visitor_id) {
        if (this.dialogData.visitor_id === this.selectedVisitorID) {
          return
        }
      }

      if (this.selectedVisitorID) {
        getVisitor(this.selectedVisitorID).then(response => {
          this.dialogData.visitor = response.data;
          this.dialogData.visitor_id = this.selectedVisitorID;
        });
      }
    }

  },
  beforeMount() {
    this.fetchData();
    this.initForm();
  },
  methods: {
    initForm() {
      getListDept().then(response => {
        this.depts = response.data;
      });
      // console.log(this.depts);
    },
    fetchData() {
      this.listLoading = true;
      this.img1 = null;
      this.img2 = null;


      if (this.activeMenu === '1'){
        getOngoingVisit().then(response => {
          this.data = response.data;
          this.listLoading = false;
          this.setTimers();
        })
      }
      else if (this.activeMenu === '2'){
        getCurrentAppointment().then(response => {
          this.data = response.data;
          this.listLoading = false;
        })
      }
      else if (['3','4'].includes(this.activeMenu)){

        if (!this.filterperiod) return
        const dt1 = this.filterperiod[0];
        const dt2 = this.filterperiod[1];

        if (!dt1 ||  !dt2) return;


        // if (!this.filtertxt) return;
        if (this.activeMenu === '3') {
          searchVisit(dt1, dt2, this.filtertxt).then(response => {
            this.data = response.data;
            this.listLoading = false;
            // this.setTimers();
          })
        }
        else if (['4'].includes(this.activeMenu)) {
          searchDocument(dt1, dt2, this.filtertxt).then(response => {
            this.data = response.data;
            this.listLoading = false;
            // this.setTimers();
          })
        }
      }

    },
    searchVisitor(query) {
      if (query !== '') {
        this.visitorloading = true;

        getListVisitor(query).then(response => {
          this.visitors = response.data;
          this.visitorLoading = false;
        })
      } else {
        this.visitors = [];
      }
    },
    searchEmployee(query) {
      if (query !== '') {
        this.employeeLoading = true;
        getListEmployee(this.param_department_id, query).then(response => {
          this.listemployee = response.data;
          this.employeeLoading = false;
        })
      } else {
        this.visitors = [];
      }
    },
    setTimers() {
      this.data = this.data.map(rec => ({
        ...rec,
        elapsed: '',
        startTimeAsDate: new Date(rec.entrydate)
      }));

      this.data.map(rec => {
        // console.log('this');
        const event = setInterval(() => {
          rec.elapsed = getElapsedTime(rec.startTimeAsDate);
          // rec.elapsed = new Date(currentDate.getTime() - rec.startTimeAsDate).toLocaleTimeString([], { hour12: false });
        }, 1000);

        this.intervalEvents.push(event);
      });
    },
    showDialog(id) {
      // console.log(new Date());
      this.img1 = null;
      this.img2 = null;
      // this.loadingDialog = true;
      this.selectedVisitorID = null;
      this.$set(this, 'selectedVisitorID', null);

      getVisit(id).then(response => {
        this.dialogData = response.data;
        this.visitors = [];
        this.listemployee = [];
        // console.log(this.dialogData);
        if (id === 0) {
          this.param_department_id = null;
          this.dialogData = {
            caption: 'Tambah Data Kunjungan',
            visitor: {},
            department: {}
          };
        } else {
          this.dialogData.caption = 'Edit Data Kunjungan';
          if (this.dialogData.department) {
            this.param_department_id = this.dialogData.department.id
          }
          this.visitors.push(this.dialogData.visitor);
          this.selectedVisitorID = this.dialogData.visitor_id;
        }

        this.img1 = getVisitImgURL(this.dialogData.imgpath1);
        this.img2 = getVisitImgURL(this.dialogData.imgpath2);

        this.dialogVisible = true;
      })
    },
    showAppointment(id) {
      // this.activeMenu = '1';
      this.img1 = null;
      this.img2 = null;
      this.selectedVisitorID = null;
      this.$set(this, 'selectedVisitorID', null);

      getAppointment(id).then(response => {
        this.dialogData = {
          id: 0,
          caption: 'Tambah Data Kunjungan',
          visitor: response.data.visitor,
          department: response.data.department,
          visitor_id: response.data.visitor_id,
          dept_id: response.data.dept_id,
          reason: response.data.reason,
          person_to_meet: response.data.person_to_meet,
          appointment_id: response.data.id
        };

        if (this.dialogData.department) {
          this.param_department_id = this.dialogData.department.id
        }
        this.visitors.push(this.dialogData.visitor);
        this.selectedVisitorID = this.dialogData.visitor_id;

        this.dialogVisible = true;
      })
    },
    showDialogPhoto(idx) {
      this.dialogPhotoActiveIdx = idx;
      this.dialogPhotoVisible = true;
    },
    handleCloseDlg(done) {
      this.$confirm('Anda yakin menutup form ini?')
        .then(_ => {
          done();
        })
        .catch(_ => {});
    },
    handleEdit(index, row, viewonly = false) {
      this.dialogReadOnly = viewonly;
      this.showDialog(row.id);
      // this.$router.push({ name: 'update_department', params: { id: row.id }})
    },
    handleAppointment(index, row) {
      this.dialogReadOnly = false;
      this.showAppointment(row.appointment_id);
      // this.$router.push({ name: 'update_department', params: { id: row.id }})
    },
    handleNew(isdoc = false) {
      // this.isdocument = isdoc;
      this.showDialog(0);
      // this.$router.push({ path: '/master/update_department' })
    },
    handleSelect(key, keyPath) {
      // console.log(key, keyPath);
      this.activeMenu  = key;
      this.fetchData()
    },
    getVisitorNameFromAR(id){
      for (var v in this.data){
        if (v.id === id){
          return v;
        }
      }
      return null
    },
    handleEndVisit(row){
      var vm = this;

      this.$confirm('Anda yakin mengkhiri kunjungan atas tamu : ' + row.visitorname ).then(_ => {
        endVisit(row.id).then(response => {
          vm.fetchData();
        })
      })
    },
    saveData() {
      var vm = this;
      this.dialogData.dept_id = this.param_department_id;

      if (this.activeMenu === '4') {
        this.dialogData.isdocument = 1
      } else {
        this.dialogData.isdocument = 0
      }

      postVisit(this.dialogData, this.img1, this.img2).then(response => {
        vm.$message({
          type: 'success',
          message: 'Data Berhasil Disimpan'
        });
        vm.dialogVisible = false;
        vm.fetchData();
      })
    },
    onCapture() {
      if (this.dialogPhotoActiveIdx == 1){
        this.img1 = this.$refs.webcam.capture();
      }else{
        this.img2 = this.$refs.webcam.capture();
      }
      this.dialogPhotoVisible = false;
    },
    onStarted(stream) {
      // console.log("On Started Event", stream);
    },
    onStopped(stream) {
      // console.log("On Stopped Event", stream);
    },
    onStop() {
      this.$refs.webcam.stop();
    },
    onStart() {
      this.$refs.webcam.start();
    },
    onError(error) {
      console.log('On Error Event', error);
    },
    onCameras(cameras) {
      this.devices = cameras;
      // //empty item__label
      // this.devices.forEach(function(item) {
      //   item.label = 'Camera 1';//setting the value
      //   delete item.num;//deleting the num from the object
      // });
      // console.log(this.devices);
      // console.log("On Cameras Event", this.devices);
    },
    onCameraChange(deviceId) {
      this.deviceId = deviceId;
      this.camera = deviceId;
      // console.log("On Camera Change Event", deviceId);
    },
    handleChangePeriod(){
      this.fetchData()
    },
    handleChangeInputSearch(){
      // console.log('handleChangeInputSearch');
      this.fetchData()
    }
  }
}
</script>

<style scoped>
  .el-table >>> .cell {
    word-break: break-word;
    white-space: pre-wrap;
  }
  .el-table >>> thead {
    color: rgb(191, 203, 217);
    font-weight: 500;
    background: #304156;
  }
  .el-table >>> th {
    background: #304156;
  }
  .el-form-item {
    margin-bottom: 15px;
  }
</style>

<style>
   .el-descriptions-item__cell.el-descriptions-item__label.is-bordered-label {
     width: 120px;
     color: #909399;
     background: #fafafa;
   }

   .inputdialog .el-dialog__header{
     display: none;
   }

   .inputdialog .el_dialog{
     margin-top: 5vh;
   }
</style>
