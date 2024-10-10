<template>
  <div class="app-container">
    <el-table
      :v-loading="listLoading"
      :data="data.filter(data => !search || data.username.toLowerCase().includes(search.toLowerCase()))"
      style="width: 100%"
    >
      <el-table-column label="User Name" prop="username" />
      <el-table-column
        align="right"
      >
        <template slot="header" slot-scope="scope">
          <el-input
            v-model="search"
            size="mini"
            placeholder="Type to search"
          />
          <span hidden>{{ scope.row }}</span>
        </template>
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.deptcode !== null "
            size="mini"
            @click="handleEdit(scope.$index, scope.row)"
          >Edit</el-button>
          <el-button
            v-if="scope.row.deptcode !== null "
            size="mini"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)"
          >Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
    <br>
    <el-button type="success" icon="el-icon-plus" @click.native.prevent="showDialog(0)">Add New User</el-button>
    <el-dialog :title="dialogData.caption" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="dialogData" label-width="120px">
        <el-form-item label="User Name">
          <el-input v-model="dialogData.username" />
        </el-form-item>
        <el-form-item label="Full Name">
          <el-input v-model="dialogData.fullname" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="dialogData.password" show-password />
        </el-form-item>
        <el-form-item label="Cabang">
          <el-select v-model="dialogData.department_id" placeholder="Select Cabang" style="width:100%">
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
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click.native.prevent="saveData()">Update</el-button>
        <el-button @click="dialogVisible = false">Cancel</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUsers, getUser, delUser, postUser } from '@/api/users'

export default {
  data() {
    return {
      data: [],
      depts: [],
      listLoading: true,
      search: '',
      dialogData: {
        caption: ''
      },
      dialogVisible: false
    }
  },
  created() {
    // this.fetchDepts()
    this.fetchData()
  },
  methods: {
    fetchDepts() {
      // getListDept().then(response => {
      //   this.depts = response.data;
      // })
    },
    fetchData() {
      this.listLoading = true
      getUsers().then(response => {
        this.data = response.data;
        this.listLoading = false
      })
    },
    handleEdit(index, row) {
      this.showDialog(row.id);
    },
    handleDelete(index, row) {
      var vm = this;
      this.$confirm('Anda yakin menghapus user ini?', 'Warning', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        delUser(row.id).then(response => {
          vm.fetchData();
        })
      })
    },
    showDialog(id) {
      getUser(id).then(response => {
        this.dialogData = response.data;
        if (id === 0) {
          this.dialogData = { caption: '' }
        } else {
          this.dialogData.caption = 'Edit User';
        }

        this.dialogVisible = true;
      })
    },
    saveData() {
      var vm = this;
      postUser(this.dialogData).then(response => {
        vm.$message({
          type: 'success',
          message: 'Data Berhasil Disimpan'
        });
        vm.dialogVisible = false;
        vm.fetchData();
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
