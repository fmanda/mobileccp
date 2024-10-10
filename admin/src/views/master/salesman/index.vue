<template>
  <div class="app-container">
    <el-table
      :v-loading="listLoading"
      :data="data.filter(data => !search || data.username.toLowerCase().includes(search.toLowerCase()))"
      style="width: 100%"
    >
      <el-table-column label="Nama Lengkap" prop="nama" />
      <el-table-column label="Kode Dimensy" prop="kode" />
      <el-table-column label="User Name" prop="username" />
      <el-table-column label="ID/External Code" prop="id" />
    </el-table>
    
  </div>
</template>

<script>
import { getSalesman } from '@/api/salesman'

export default {
  data() {
    return {
      data: [],
      listLoading: true,
      search: ''
    }
  },
  created() {
    // this.fetchDepts()
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.listLoading = true
      getSalesman().then(response => {
        this.data = response.data;
        this.listLoading = false
      })
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
