<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-input
          v-model="filterText"
          placeholder="Search File / Directory"
        />
      </div>
      <span>Directory List :</span>
      <p />
      <el-tree
        ref="tree"
        class="filter-tree"
        :data="data"
        :props="defaultProps"
        :filter-node-method="filterNode"
        icon-class="el-icon-caret-right"
      />
    </el-card>
  </div>
</template>

<script>
import { getDirectory } from '@/api/directory'

export default {
  data() {
    return {
      filterText: '',
      data: null,
      listLoading: true,
      defaultProps: {
        children: 'items',
        label: 'fileName'
      }
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.listLoading = true
      getDirectory().then(response => {
        this.data = response.data.items;
        this.listLoading = false
      })
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.fileName.toLowerCase().indexOf(value.toLowerCase()) !== -1;
    }
  }
}
</script>
