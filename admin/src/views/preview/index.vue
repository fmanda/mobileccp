<template>
  <div class="app-container">
    <el-button-group style="width:100%; margin-bottom:10px">
      <el-button type="primary" icon="el-icon-back" style="width:120px" @click.native.prevent="back()">Back</el-button>
      <el-button type="success">Department : {{ param_dept }}</el-button>
      <el-button type="success">Sub : {{ param_subcode }}</el-button>
      <el-button type="success">Level : {{ param_level }}</el-button>
    </el-button-group>
    <!-- <el-table :data="getHeader()" border style="margin-bottom:10px;" :show-header="false" :cell-style="cellStyle2">
      <el-table-column prop="caption" label="" width="120"></el-table-column>
      <el-table-column prop="value" label="" ></el-table-column>
      </el-table-column>
    </el-table> -->

    <div v-for="(item, index) in data" :key="item.id">
      <el-table :data="getData(index)" border style="width: 100%" :show-header="false" :cell-style="cellStyle">
        <el-table-column prop="caption" label="" width="120" />
        <el-table-column label="">
          <template slot-scope="sc">
            <div v-if="(!sc.row.islink)&&(!sc.row.isimg)">{{ sc.row.value }}</div>
            <el-link v-if="sc.row.islink" :href="sc.row.value" type="primary" style="margin-bottom: 10px">Download File</el-link>
            <el-image v-if="sc.row.isimg" :src="sc.row.value" />
          </template>
        </el-table-column>
      </el-table>
      <br>
    </div>
    <!-- <table>
      <thead>
        <tr>
          <th>Month</th>
          <th>Savings</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>January</td>
          <td>$100</td>
        </tr>
      </tbody>

    </table> -->
    <!-- <el-image
      style="width: 200px; height: 200px"
      :src="url" fit="contains"
      :preview-src-list="[url]">
    </el-image> -->
  </div>

</template>
<script>
import { getFileListML, getFileListKPI } from '@/api/kpidept'
import { getDeptHeader } from '@/api/product'

export default {
  data() {
    return {
      data: [],
      param_year: 2020,
      param_dept: '',
      param_deptid: 0,
      param_subcode: '',
      param_level: 0,
      param_iskpi: false,
      param_sender: 'upload'
    }
  },
  mounted() {
    // console.log(this.$route.params);
    if (this.$route.params) {
      this.param_deptid = this.$route.params.deptid;
      this.param_subcode = this.$route.params.subcode;
      this.param_level = this.$route.params.level;
      this.param_year = this.$route.params.year;
      this.param_iskpi = this.$route.params.iskpi;
      this.param_sender = this.$route.params.sender;
      this.fetchData(
        this.param_year,
        this.param_deptid,
        this.param_subcode,
        this.param_level,
        this.param_iskpi
      );
    }
  },
  methods: {
    fetchData(year, dept_id, subcode, level, iskpi) {
      if (iskpi) {
        getFileListKPI(year, dept_id, subcode, level).then(response => {
          this.data = response.data;
        }).catch(() => {
          this.data = [];
        })
      } else {
        getFileListML(year, dept_id, subcode, level).then(response => {
          this.data = response.data;
        }).catch(() => {
          this.data = [];
        })
      }
      getDeptHeader(dept_id).then(response => {
        this.param_dept = response.data.deptname;
      }).catch(() => {
        this.param_dept = ''
      })
    },
    getData(index) {
      var item = this.data[index];
      var ar = [];
      ar.push({ caption: 'File Name', value: item.filename, link: process.env.VUE_APP_BASE_URL + '/downloadfile/' + item.id.toString() });
      ar.push({ caption: 'User Upload', value: item.username });
      ar.push({ caption: 'Upload Date', value: item.upload_date });

      if (item.filename.match(/.(jpg|jpeg|png|gif)$/i)) {
        ar.push({ caption: '', value: (
          process.env.VUE_APP_BASE_URL + '/downloadfile/' + item.id.toString()
        ), isimg: true });
      } else {
        ar.push({ caption: '', value: (
          process.env.VUE_APP_BASE_URL + '/downloadfile/' + item.id.toString()
        ), islink: true });
      }

      return ar;
    },
    getHeader() {
      var ar = [];
      ar.push({ caption: 'Department', value: (this.param_dept + '.   Sub Area : ' + this.param_subcode + '.   Level ' + this.param_level.toString()) });
      ar.push({ caption: 'Total Files', value: this.data.length.toString() + '.   Year : ' + this.param_year });
      return ar;
    },
    cellStyle({ row, column, rowIndex, columnIndex }) {
      var str = '';
      if (columnIndex === 1 && rowIndex === 0) {
        str = str + ' font-weight: bold;'
      }
      if (columnIndex === 0) {
        str = str + ' background-color: #304156; color: rgb(191, 203, 217);'
      }

      return str;
    },
    cellStyle2({ row, column, rowIndex, columnIndex }) {
      var str = '';
      if (columnIndex === 0) {
        str = str + ' background-color: #304156; color: rgb(191, 203, 217);'
      }
      return str;
    },
    back() {
      // this.$router.go(-1);
      // console.log(this.param_sender);
      this.$router.push({
        name: this.param_sender,
        params: {
          year: this.param_year,
          deptid: this.param_deptid
        }
      })
    }
  }
}
</script>

<style scoped>
  .el-table >>> .cell {
    line-height: 1;
    font-size: 12px;
  }

</style>
