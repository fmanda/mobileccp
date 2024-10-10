<template>
  <div class="app-container">
    <el-form ref="kpidept" :model="kpidept" label-width="120px" label-position="top">
      <el-form-item label="Parameter">
        <el-col :span="10" style="margin-left: 10px;">
          <el-select v-model="param_department_id" placeholder="Select Department" style="width:100%">
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
        <el-col :span="5" style="margin-left: 10px;">
          <el-select v-model="param_period" placeholder="Select Period" style="width:100%;">
            <el-option
              v-for="period in periods"
              :key="period.id"
              :label="period.caption"
              :value="period.id"
            >
              {{ period.caption }}
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="2" style="margin-left: 10px;">
          <el-button type="primary" style="width: 100%;" icon="el-icon-success" @click.native.prevent="saveData()">Register</el-button>
        </el-col>
        <el-col :span="3" style="margin-left: 10px;">
          <el-button type="success" style="width: 100%;" icon="el-icon-delete" @click.native.prevent="copyFromPrev()">Copy From Previous</el-button>
        </el-col>
      </el-form-item>
    </el-form>

    <el-tabs type="border-card">
      <el-tab-pane>
        <span slot="label"><i class="el-icon-s-opportunity" /> Maturity Level</span>
        <el-table :data="kpidept.mlitems" style="width: 100%">
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
              <el-button plain type="danger" size="small" icon="el-icon-delete" @click.native.prevent="deleteRow(scope.$index, kpidept.mlitems)" />
              <el-button plain size="small" icon="el-icon-top" @click.native.prevent="moveRow(scope.$index, kpidept.mlitems,-1) " />
              <el-button plain size="small" icon="el-icon-bottom" @click.native.prevent="moveRow(scope.$index, kpidept.mlitems,1) " />
            </template>
          </el-table-column>
        </el-table>
        <br>
        <el-button type="success" icon="el-icon-plus" @click.native.prevent="addRow(kpidept.mlitems)">Add ML Area</el-button>
      </el-tab-pane>

      <el-tab-pane>
        <span slot="label"><i class="el-icon-s-data" /> KPI Level</span>
        <el-table :data="kpidept.kpiitems" style="width: 100%">
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
              <el-button plain type="danger" size="small" icon="el-icon-delete" @click.native.prevent="deleteRow(scope.$index, kpidept.kpiitems)" />
              <el-button plain size="small" icon="el-icon-top" @click.native.prevent="moveRow(scope.$index, kpidept.kpiitems,-1) " />
              <el-button plain size="small" icon="el-icon-bottom" @click.native.prevent="moveRow(scope.$index, kpidept.kpiitems,1) " />
            </template>
          </el-table-column>
        </el-table>
        <br>
        <el-button type="success" icon="el-icon-plus" @click.native.prevent="addRow(kpidept.kpiitems)">Add KPI Area</el-button>
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
import { getListDept } from '@/api/product'
import { getPeriod, getKPIDept, postKPIDept, getKPIDeptPrev } from '@/api/kpidept'

export default {
  data() {
    return {
      editing: null,
      kpidept: {
        period: 0,
        department_id: 0,
        mlitems: [],
        kpiitems: []
      },
      dialogData: {},
      dialogVisible: false,
      periods: [],
      depts: [],
      param_period: null,
      param_department_id: null
    }
  },
  watch: {
    param_period(val) {
      this.fetchData();
    },
    param_department_id(val) {
      this.fetchData();
    }
  },
  beforeMount() {
    this.fetchData();
    this.initForm();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      if (!this.param_department_id || this.param_department_id === 0 || !this.param_period) {
        this.kpidept = {
          id: 0,
          period: this.param_period,
          department_id: 0,
          mlitems: [],
          kpiitems: []
        }
      } else {
        getKPIDept(this.param_department_id, this.param_period).then(response => {
          this.kpidept = response.data;

          if (!this.kpidept) {
            this.kpidept = {
              id: 0,
              period: this.param_period,
              department_id: this.param_department_id,
              mlitems: [],
              kpiitems: []
            }
          }
          this.loading = false
        })
      }
    },
    copyFromPrev(){
      this.listLoading = true;
      if (!this.param_department_id || this.param_department_id === 0 || !this.param_period) {
        this.kpidept = {
          id: 0,
          period: this.param_period,
          department_id: 0,
          mlitems: [],
          kpiitems: []
        }
      } else {
        getKPIDeptPrev(this.param_department_id, this.param_period).then(response => {
          this.kpidept = response.data;

          if (!this.kpidept) {
            this.kpidept = {
              id: 0,
              period: this.param_period,
              department_id: this.param_department_id,
              mlitems: [],
              kpiitems: []
            }
          }
          this.loading = false
        })
      }
    },
    initForm() {
      getListDept().then(response => {
        this.depts = response.data;
      });
      getPeriod().then(response => {
        this.periods = response.data;
      })

      //default periods
      var today = new Date();
      var smst = Math.floor((today.getMonth() + 5) / 6);

      this.param_period = today.getFullYear().toString() +  ('0' + smst.toString()).substring(0,2);
    },
    back() {
      // fetchData()
      // this.$router.push({ path: '/master/department' })
    },
    saveData() {
      this.$confirm('Anda yakin Data Sudah Sesuai?', 'Confirmation', {
        cancelButtonText: 'Cancel',
        confirmButtonText: 'OK',
        type: 'warning'
      }).then(() => {
        this.postData()
      })
    },
    postData() {
      var vm = this;
      this.kpidept.department_id = this.param_department_id;
      this.kpidept.periods = this.param_period;
      postKPIDept(this.kpidept).then(response => {
        vm.$message({
          type: 'success',
          message: 'Data Berhasil Disimpan'
        });
        vm.kpidept = response.data;
      })
    },
    deleteRow(index, items) {
      items.splice(index, 1)
    },
    addRow(items) {
      items.push({ areacode: '', areaname: '', items: [] })
    },
    addSubRow(items) {
      // console.log(items);
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
