<template>
  <div class="app-container">
    <el-form ref="department" :model="department" label-width="120px" label-position="top">
      <el-form-item label="Department">
        <el-col :span="2">
          <el-input v-model="department.deptcode" placeholder="Dept Code" style="width: 90%;" />
        </el-col>
        <el-col :span="10">
          <el-input v-model="department.deptname" placeholder="Department Name" />
        </el-col>
        <el-col :span="2" style="margin-left: 10px;">
          <el-button type="primary" style="width: 100%;" icon="el-icon-success" @click.native.prevent="saveData()">Save</el-button>
        </el-col>
        <el-col :span="2" style="margin-left: 10px;">
          <el-button type="danger" style="width: 100%;" icon="el-icon-delete" @click.native.prevent="back()">Cancel</el-button>
        </el-col>
      </el-form-item>
    </el-form>

    <el-tabs type="border-card">
      <el-tab-pane>
        <span slot="label"><i class="el-icon-s-opportunity" /> Maturity Level</span>
        <el-table :data="department.mlitems" style="width: 100%">
          <el-table-column type="expand">
            <template slot-scope="props">
              <el-card class="box-card">
                <el-table :data="props.row.items" style="width: 100%">
                  <el-table-column prop="subcode" label="Sub Code" width="120">
                    <template slot-scope="sc">
                      <el-input v-model="sc.row.subcode" placeholder="Sub Code" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="subname" label="Sub Area Name" style="width: 100%">
                    <template slot-scope="sc">
                      <el-input v-model="sc.row.subname" placeholder="Sub Name" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="weight" label="Bobot" width="160">
                    <template slot-scope="sc">
                      <!-- <el-input type="number" step="0.01" v-model="sc.row.weight" placeholder="Weight" /> -->
                      <el-input-number v-model="sc.row.weight" :precision="3" :step="0.1" :max="10" />
                    </template>
                  </el-table-column>
                  <el-table-column label="Operations">
                    <template slot-scope="sc">
                      <el-button plain type="danger" size="small" icon="el-icon-delete" @click.native.prevent="deleteRow(sc.$index, props.row.items)" />
                      <el-button plain size="small" icon="el-icon-top" @click.native.prevent="moveRow(sc.$index, props.row.items,-1) " />
                      <el-button plain size="small" icon="el-icon-bottom" @click.native.prevent="moveRow(sc.$index, props.row.items,1) " />
                      <el-button plain type="primary" size="small" icon="el-icon-info" @click.native.prevent="showDialog(sc.$index, props.row.items)">Description</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <br>
                <el-button plain type="primary" icon="el-icon-plus" @click.native.prevent="addSubRow(props.row.items)">Add Sub Area</el-button>
              </el-card>
            </template>
          </el-table-column>
          <el-table-column prop="areacode" label="Area Code" width="120">
            <template slot-scope="scope">
              <el-input v-model="scope.row.areacode" placeholder="Area Code" />
            </template>
          </el-table-column>
          <el-table-column prop="areacode" label="Area Name" width="300">
            <template slot-scope="scope">
              <el-input v-model="scope.row.areaname" placeholder="Area Name" />
            </template>
          </el-table-column>
          <el-table-column label="Operations">
            <template slot-scope="scope">
              <el-button plain type="danger" size="small" icon="el-icon-delete" @click.native.prevent="deleteRow(scope.$index, department.mlitems)" />
              <el-button plain size="small" icon="el-icon-top" @click.native.prevent="moveRow(scope.$index, department.mlitems,-1) " />
              <el-button plain size="small" icon="el-icon-bottom" @click.native.prevent="moveRow(scope.$index, department.mlitems,1) " />
            </template>
          </el-table-column>
        </el-table>
        <br>
        <el-button type="success" icon="el-icon-plus" @click.native.prevent="addRow(department.mlitems)">Add ML Area</el-button>
      </el-tab-pane>

      <el-tab-pane>
        <span slot="label"><i class="el-icon-s-data" /> KPI Level</span>
        <el-table :data="department.kpiitems" style="width: 100%">
          <el-table-column type="expand">
            <template slot-scope="props">
              <el-card class="box-card">
                <el-table :data="props.row.items" style="width: 100%">
                  <el-table-column prop="subcode" label="Sub Code" width="120">
                    <template slot-scope="sc">
                      <el-input v-model="sc.row.subcode" placeholder="Sub Code" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="subname" label="Sub Area Name" style="width: 100%">
                    <template slot-scope="sc">
                      <el-input v-model="sc.row.subname" placeholder="Sub Name" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="weight" label="Bobot" width="160">
                    <template slot-scope="sc">
                      <!-- <el-input type="number" step="0.01" v-model="sc.row.weight" placeholder="Weight" /> -->
                      <el-input-number v-model="sc.row.weight" :precision="3" :step="0.1" :max="10" />
                    </template>
                  </el-table-column>
                  <el-table-column label="Operations">
                    <template slot-scope="sc">
                      <el-button plain type="danger" size="small" icon="el-icon-delete" @click.native.prevent="deleteRow(sc.$index, props.row.items)" />
                      <el-button plain size="small" icon="el-icon-top" @click.native.prevent="moveRow(sc.$index, props.row.items,-1) " />
                      <el-button plain size="small" icon="el-icon-bottom" @click.native.prevent="moveRow(sc.$index, props.row.items,1) " />
                      <el-button plain type="primary" size="small" icon="el-icon-info" @click.native.prevent="showDialog(sc.$index, props.row.items)">Description</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <br>
                <el-button plain type="primary" icon="el-icon-plus" @click.native.prevent="addSubRow(props.row.items)">Add Sub Area</el-button>
              </el-card>
            </template>
          </el-table-column>
          <el-table-column prop="areacode" label="Area Code" width="120">
            <template slot-scope="scope">
              <el-input v-model="scope.row.areacode" placeholder="Area Code" />
            </template>
          </el-table-column>
          <el-table-column prop="areacode" label="Area Name" width="300">
            <template slot-scope="scope">
              <el-input v-model="scope.row.areaname" placeholder="Area Name" />
            </template>
          </el-table-column>
          <el-table-column label="Operations">
            <template slot-scope="scope">
              <el-button plain type="danger" size="small" icon="el-icon-delete" @click.native.prevent="deleteRow(scope.$index, department.kpiitems)" />
              <el-button plain size="small" icon="el-icon-top" @click.native.prevent="moveRow(scope.$index, department.kpiitems,-1) " />
              <el-button plain size="small" icon="el-icon-bottom" @click.native.prevent="moveRow(scope.$index, department.kpiitems,1) " />
            </template>
          </el-table-column>
        </el-table>
        <br>
        <el-button type="success" icon="el-icon-plus" @click.native.prevent="addRow(department.kpiitems)">Add KPI Area</el-button>
      </el-tab-pane>
      <el-tab-pane label="Users">Users</el-tab-pane>
    </el-tabs>

    <el-dialog :title="dialogData.subname" :visible.sync="dialogVisible" label-position="top">
      <el-input
        v-model="dialogData.subdesc"
        type="textarea"
        :autosize="{ minRows: 5, maxRows: 5}"
        placeholder="Please input Description"
      />
      <br>
      <el-row :gutter="10">
        <el-col :span="8">
          <el-input v-model="dialogData.level_1" type="textarea" rows="4" placeholder="Level 1" />
        </el-col>
        <el-col :span="16">
          <el-input v-model="dialogData.leveldetail_1" type="textarea" rows="4" placeholder="Uraian Level 1" />
        </el-col>
      </el-row>

      <el-row :gutter="10">
        <el-col :span="8">
          <el-input v-model="dialogData.level_2" type="textarea" rows="4" placeholder="Level 2" />
        </el-col>
        <el-col :span="16">
          <el-input v-model="dialogData.leveldetail_2" type="textarea" rows="4" placeholder="Uraian Level 2" />
        </el-col>
      </el-row>

      <el-row :gutter="10">
        <el-col :span="8">
          <el-input v-model="dialogData.level_3" type="textarea" rows="4" placeholder="Level 3" />
        </el-col>
        <el-col :span="16">
          <el-input v-model="dialogData.leveldetail_3" type="textarea" rows="4" placeholder="Uraian Level 3" />
        </el-col>
      </el-row>

      <el-row :gutter="10">
        <el-col :span="8">
          <el-input v-model="dialogData.level_4" type="textarea" rows="4" placeholder="Level 4" />
        </el-col>
        <el-col :span="16">
          <el-input v-model="dialogData.leveldetail_4" type="textarea" rows="4" placeholder="Uraian Level 4" />
        </el-col>
      </el-row>

      <el-row :gutter="10">
        <el-col :span="8">
          <el-input v-model="dialogData.level_5" type="textarea" rows="4" placeholder="Level 5" />
        </el-col>
        <el-col :span="16">
          <el-input v-model="dialogData.leveldetail_5" type="textarea" rows="4" placeholder="Uraian Level 5" />
        </el-col>
      </el-row>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">Update</el-button>
        <el-button @click="dialogVisible = false">Cancel</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
// import { test } from '@/api/test'
import { getDepartment, postDepartment } from '@/api/product'

export default {
  data() {
    return {
      editing: null,
      department: {
        id: 0,
        deptcode: '',
        deptname: '',
        mlitems: [],
        kpiitems: []
      },
      dialogData: {},
      dialogVisible: false
    }
  },
  beforeMount() {
    var id = 0;
    console.log(this.$route.params);
    if (this.$route.params) {
      id = this.$route.params.id
    }
    this.fetchData(id);
  },
  methods: {
    fetchData(id) {
      this.listLoading = true;
      if (!id || id === 0) {
        this.department = {
          id: 0,
          deptcode: '',
          deptname: '',
          mlitems: [],
          kpiitems: []
        }
      } else {
        getDepartment(id).then(response => {
          this.department = response.data
          this.loading = false
        })
      }
    },
    back() {
      this.$router.push({ path: '/master/department' })
    },
    saveData() {
      this.$confirm('Anda yaking Data Sudah Sesuai?', 'Confirmation', {
        cancelButtonText: 'Cancel',
        confirmButtonText: 'OK',
        type: 'warning'
      }).then(() => {
        this.postData()
      })
    },
    postData() {
      var vm = this;
      postDepartment(this.department).then(response => {
        vm.$message({
          type: 'success',
          message: 'Data Berhasil Disimpan'
        });
        vm.back();
      })
    },
    deleteRow(index, items) {
      items.splice(index, 1)
    },
    addRow(items) {
      items.push({ areacode: '', areaname: '' })
    },
    addSubRow(items) {
      items.push({ subcode: '', subname: '' })
    },
    moveRow(index, items, inc) {
      var newindex = index + inc
      // console.log(newindex);
      if (newindex < 0) return
      if (newindex >= items.length) return
      var tmp = items[index]
      items[index] = items[newindex]
      // items[newindex] = tmp
      this.$set(items, newindex, tmp)
    },
    showDialog(index, items) {
      this.dialogData = items[index];
      this.dialogVisible = true;
    }
  }
}
</script>

<style scoped>
  .el-input-number {
    width: 140px;
  }
  .el-row {
    margin-top: 10px;
    /* padding: 5px; */
    /* background-color: #2cfb6954; */
  }
</style>
