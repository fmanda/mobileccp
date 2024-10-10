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
      <!-- <el-menu-item index="1"><i class="el-icon-user"></i>Akan Berlangsung</el-menu-item> -->
      <el-menu-item index="1"><i class="el-icon-date"></i>Semua Kunjungan</el-menu-item>
    </el-menu>

    <div v-if= "activeMenu == '1' ">
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
            
            <el-descriptions-item label-class-name="my-label">
              <template slot="label">
                <i class="el-icon-office-building"></i>
                Alamat
              </template>
              {{props.row.alamat}}
            </el-descriptions-item>
        

            <el-descriptions-item label-class-name="my-label">
              <template slot="label">
                <i class="el-icon-location-information"></i>
                Map
              </template>
                <el-link type="primary" :href="buildGMapURL(props.row.latitude, props.row.longitude)" target="_blank">{{ buildGMapURL(props.row.latitude, props.row.longitude) }}</el-link>
            </el-descriptions-item>
        
            
          </el-descriptions>
        </template>
      </el-table-column>
      <el-table-column label="visitdate" prop="visitdate" sortable/>
      <el-table-column label="salesman" prop="salesman" sortable/>
      <el-table-column label="customer" prop="customer" sortable/>
      <el-table-column label="alamat" prop="alamat" sortable/>
      <el-table-column label="GeoLocation" >
        <template slot-scope="scope">
          <el-button plain size="small" type="primary" @click="gotoMapLocation(scope.row.latitude, scope.row.longitude)" >
            <i class="el-icon-location-information"></i> Map Location</el-button>
        </template>
      </el-table-column>
      

      <!-- <el-table-column
        label="Jadwal Bertemu"
        width="180"
        sortable
        >
        <template slot-scope="scope">
          <span style="margin-left: 10px">{{ scope.row.planningdate }}</span>
        </template>
      </el-table-column> -->

      <!-- <el-table-column
        fixed="right"
        label="Status"
        width="140">
        <template slot-scope="scope">
          <el-button-group>
            <el-button plain icon="el-icon-edit-outline" size="small" type="primary" @click="handleEdit(scope.$index, scope.row)"></el-button>
            <el-button plain size="small" type="danger" @click="handleDelete(scope.$index, scope.row)" >Hapus</el-button>
          </el-button-group>
        </template>
      </el-table-column> -->

    </el-table>
    <br>

    <!-- <el-button type="primary" icon="el-icon-plus" @click.native.prevent="handleNew()">Buat Appointment</el-button> -->


    

  </div>
</template>

<script>
import { getVisitPeriod } from '@/api/salesorder'
import { formatCurrency } from '@/utils/index.js'
// import { find, head } from 'lodash';

export default {
  components: {
    // WebCam
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
      activeMenu: '1',
      
      filterperiod: [
        new Date().getTime() -(3600 * 1000 * 24 * 30),
        new Date().getTime()
      ],
      pickerOptions: {
        shortcuts: [{
          text: 'Hari Ini',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setHours(0,0,0)
            end.setHours(0,0,0)
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: 'Seminggu Terakhir',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);

            start.setHours(0,0,0)
            end.setHours(0,0,0)
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: 'Sebulan Terakhir',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);

            start.setHours(0,0,0)
            end.setHours(0,0,0)
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
  },
  computed: {
  },
  beforeMount() {
    this.fetchData();
    this.initForm();
  },
  methods: {
    initForm() {
      // getListDept().then(response => {
      //   this.depts = response.data;
      // });
      // console.log(this.depts);
    },
    fetchData() {
      this.listLoading = true;

    
      if (!this.filterperiod) return
      const dt1 = this.filterperiod[0];
      const dt2 = this.filterperiod[1];

      if (!dt1 ||  !dt2) return;
      getVisitPeriod(dt1, dt2, this.filtertxt).then(response => {
        this.data = response.data;
        this.listLoading = false;

        this.data = this.data.map(item => {
          return {
            ...item, // Keep all other properties intact
            amt: formatCurrency(item.amt)
          };
        });
        // this.setTimers();
      })
      
    },
    handleSelect(key, keyPath) {
      // console.log(key, keyPath);
      this.activeMenu  = key;
      this.fetchData()
    },
    handleChangePeriod(){
      this.fetchData()
    },
    handleChangeInputSearch(){
      // console.log('handleChangeInputSearch');
      this.fetchData()
    },
    buildGMapURL(lat, long){
      return "https://www.google.com/maps/place/" + lat.toString() + "," + long.toString()
    },
    gotoMapLocation(lat, long){
      window.open(this.buildGMapURL(lat, long))
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
    /* color: rgb(191, 203, 217); */
    background: #304156;
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
