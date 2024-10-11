<template>
  <div class="app-container">
    
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
      <el-menu-item index="1"><i class="el-icon-user"></i>Browse Customer</el-menu-item>
      <!-- <el-menu-item index="2"><i class="el-icon-user"></i>Browse New Customer</el-menu-item> -->
    </el-menu>

    <el-input
      placeholder="Pencarian"
      prefix-icon="el-icon-search"
      style = "width: 400px; float: right; margin-top: -60px; margin-right: 10px;"
      @change="handleChangeInputSearch"
      clearable
      v-model="filtertxt">
    </el-input>

    <el-alert
      v-if="activeMenu=='2'"
      title="Informasi Penting"
      type="warning"
      description="Untuk melakukan mapping Customer Baru di Dimensy, Isikan 16 Digit ID ke Kolom External Code di Dimensy dengan memilih System Mobility"
      show-icon>
    </el-alert>

    <el-table
      :v-loading="listLoading"
      :data="data.filter(data => !search || data.nama.toLowerCase().includes(search.toLowerCase()))"
      style="width: 100%"
    >
      <!-- <el-table-column v-if="activeMenu=='2'" label="ID" prop="id" /> -->
      <el-table-column label="Ship Name" prop="shipname"/>
      <!-- <el-table-column label="NIK" prop="nik" /> -->
      <el-table-column label="PartnerName" prop="partnername" />
      <el-table-column label="Area" prop="areaname" />
      <el-table-column label="Jenjang" prop="jenjang" />
      <el-table-column label="NPSPN" prop="npsn" />      
    </el-table>
    <br>
  </div>
</template>

<script>
import { getListCustomer, getListNewCustomer } from '@/api/customer'

export default {
  data() {
    return {
      data: [],
      listLoading: true,
      search: '',
      dialogData: {
        caption: ''
      },
      dialogVisible: false,
      activeMenu: '1',
      filtertxt: ''
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.listLoading = true;

      // if (this.activeMenu == '1'){
        getListCustomer(this.filtertxt).then(response => {
          this.data = response.data;
          this.listLoading = false
        })
      // }else{
      //   getListNewCustomer(this.filtertxt).then(response => {
      //     this.data = response.data;
      //     this.listLoading = false
      //   })
      // }
    },
    handleSelect(key, keyPath) {
      // console.log(key, keyPath);
      this.activeMenu  = key;
      this.fetchData()
    },
    handleChangeInputSearch(){
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
    /* color: rgb(191, 203, 217); */
    background: #304156;
  }
</style>
