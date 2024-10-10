<template>
  <div class="app-container">
    <el-alert v-if="errormsg != '' "
      :title="errormsg"
      type="error"
      show-icon>
    </el-alert>

    <div>
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
      <el-table-column label="Department" prop="deptname" sortable/>

      <el-table-column
        label="Masuk"
        width="180"
        sortable
        >
        <template slot-scope="scope">
          <span style="margin-left: 10px">{{ scope.row.entrydate }}</span>
        </template>
      </el-table-column>

      <el-table-column
        label="Keluar"
        width="180"
        sortable
        >
        <template slot-scope="scope">
          <span style="margin-left: 10px">{{ scope.row.exitdate }}</span>
        </template>
      </el-table-column>

      <el-table-column
        fixed="right"
        label="Status"
        width="140">
        <template slot-scope="scope">
          <el-button-group>
            <el-button plain icon="el-icon-view" size="small" @click="handleEdit(scope.$index, scope.row, true)"></el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>
    <br>

    <el-dialog :title="dialogData.caption" :visible.sync="dialogVisible" width="900px" :before-close="handleCloseDlg"
      class="inputdialog" top="5vh"
    >
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

            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-location-outline"></i>
                Keperluan
              </template>
              <el-input v-model="dialogData.reason">
              </el-input>
            </el-descriptions-item>

            <el-descriptions-item>
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
        <el-button type="danger"  @click="dialogVisible = false">Batal</el-button>
      </span>
    </el-dialog>


    </el-dialog>
  </div>
</template>

<script>
import { getVisit, getListVisit, postVisit, getVisitImgURL, getElapsedTime,
        endVisit, getOngoingVisit, searchVisit, searchDocument, getCurrentAppointment } from '@/api/visit'
import { find, head } from 'lodash';

export default {
  components: {
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
      img1: null,
      img2: null,
      param_department_id: null,
      depts: [],
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
      dialogReadOnly: true
    }
  },
  created() {
    // this.fetchData()
    // this.setTimers();
  },
  beforeDestroy() {

  },
  computed: {

  },
  watch: {
  },
  beforeMount() {
    this.fetchData();
    this.initForm();
  },
  methods: {
    initForm() {
    },
    fetchData() {
      this.listLoading = true;
      this.img1 = null;
      this.img2 = null;

      if (!this.filterperiod) return
      const dt1 = this.filterperiod[0];
      const dt2 = this.filterperiod[1];

      if (!dt1 ||  !dt2) return;

      searchVisit(dt1, dt2, this.filtertxt).then(response => {
        this.data = response.data;
        this.listLoading = false;
        // this.setTimers();
      })
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
    handleCloseDlg(done) {
      done();
    },
    handleEdit(index, row, viewonly = true) {
      this.dialogReadOnly = true; //always true
      this.showDialog(row.id);
      // this.$router.push({ name: 'update_department', params: { id: row.id }})
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
